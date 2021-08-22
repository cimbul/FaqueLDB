package com.cimbul.faqeldb

import com.amazon.ionelement.api.ionInt
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.partiql.lang.eval.EvaluationException

class EvaluatorTest : DescribeSpec({
    val evaluator = Evaluator()

    describe("evaluate") {
        it("should evaluate expressions") {
            evaluator.evaluate("1 + 1") shouldBe ionInt(2)
        }

        it("should interpolate parameters") {
            evaluator.evaluate("2 + ?", listOf(ionInt(3))) shouldBe ionInt(5)
        }

        it("should support queries on static data") {
            val query = """
                SELECT e.name AS employeeName,
                       e.projects[0].name AS firstProjectName
                FROM
                    {
                        'employeesNest': <<
                            {
                                'id': 3,
                                'name': 'Bob Smith',
                                'title': null,
                                'projects': [
                                    { 'name': 'AWS Redshift Spectrum querying' },
                                    { 'name': 'AWS Redshift security' },
                                    { 'name': 'AWS Aurora security' }
                                ]
                            },
                            {
                                'id': 4,
                                'name': 'Susan Smith',
                                'title': 'Dev Mgr',
                                'projects': []
                            },
                            {
                                'id': 6,
                                'name': 'Jane Smith',
                                'title': 'Software Eng 2',
                                'projects': [ { 'name': 'AWS Redshift security' } ]
                            }
                        >>
                    } AS hr,
                    hr.employeesNest AS e
            """

            evaluator.evaluate(query) shouldBe ionElement("""
                [
                    {
                      "employeeName": "Bob Smith",
                      "firstProjectName": "AWS Redshift Spectrum querying"
                    },
                    {
                      "employeeName": "Susan Smith"
                    },
                    {
                      "employeeName": "Jane Smith",
                      "firstProjectName": "AWS Redshift security"
                    }
                ]
            """)
        }

        describe("DDL") {
            describe("CREATE TABLE") {
                it("should return a table ID") {
                    val result = evaluator.evaluate("CREATE TABLE foo")

                    result.listValues.single().asStruct()["tableId"] shouldNotBe null
                }

                it("should throw an error if the table exists") {
                    evaluator.evaluate("CREATE TABLE foo")

                    shouldThrow<EvaluationException> {
                        evaluator.evaluate("CREATE TABLE foo")
                    }
                }

                it("should create an empty table") {
                    evaluator.evaluate("CREATE TABLE foo")

                    val result = evaluator.evaluate("SELECT * FROM foo")

                    result.listValues.shouldBeEmpty()
                }
            }

            describe("DROP TABLE") {
                it("should throw an error if the table does not exist") {
                    shouldThrow<EvaluationException> {
                        evaluator.evaluate("DROP TABLE foo")
                    }
                }

                it("should return the table ID") {
                    val createResult = evaluator.evaluate("CREATE TABLE foo")
                    val createResultStruct = createResult.listValues.single().asStruct()
                    val tableId = createResultStruct["tableId"]

                    val dropResult = evaluator.evaluate("DROP TABLE foo")

                    val dropResultStruct = dropResult.listValues.single().asStruct()
                    dropResultStruct["tableId"] shouldBe tableId
                }

                it("should remove the table from the namespace") {
                    evaluator.evaluate("CREATE TABLE foo")

                    evaluator.evaluate("DROP TABLE foo")

                    shouldThrow<EvaluationException> {
                        evaluator.evaluate("SELECT * FROM foo")
                    }
                }
            }
        }

        describe("DML") {
            evaluator.evaluate("CREATE TABLE foo")

            describe("INSERT INTO table VALUE value") {
                it("should insert the record into the table") {
                    evaluator.evaluate("INSERT INTO foo VALUE {'bar': 'quux'}")

                    evaluator.evaluate("SELECT * FROM foo") shouldBe ionElement("""
                        [{bar: "quux"}]
                    """)
                }

                it("should preserve existing records") {
                    evaluator.evaluate("INSERT INTO foo VALUE {'a': 1}")
                    evaluator.evaluate("INSERT INTO foo VALUE {'a': 2}")

                    evaluator.evaluate("SELECT * FROM foo") shouldBe ionElement("""
                       [{a: 1}, {a: 2}]
                    """)
                }

                it("should return the document ID of the record") {
                    val result = evaluator.evaluate("INSERT INTO foo VALUE {'bar': 'quux'}")

                    result.listValues.single().asStruct()["documentId"] shouldNotBe null
                }
            }

            describe("INSERT INTO table values") {
                it("should insert the records into the table") {
                    evaluator.evaluate("INSERT INTO foo << {'a': 1}, {'a': 2} >>")

                    evaluator.evaluate("SELECT * FROM foo") shouldBe ionElement("""
                       [{a: 1}, {a: 2}]
                    """)
                }

                it("should return the document IDs for each record") {
                    val result = evaluator.evaluate("INSERT INTO foo << {'a': 1}, {'a': 2} >>")

                    result.listValues shouldHaveSize 2
                    result.listValues.forAll { x ->
                        x.asStruct()["documentId"] shouldNotBe null
                    }
                }
            }
        }
    }
})
