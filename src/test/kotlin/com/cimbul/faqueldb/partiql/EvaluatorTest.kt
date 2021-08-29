package com.cimbul.faqueldb.partiql

import com.amazon.ionelement.api.AnyElement
import com.amazon.ionelement.api.IonElement
import com.amazon.ionelement.api.ionInt
import com.amazon.ionelement.api.ionListOf
import com.amazon.ionelement.api.ionStructOf
import com.cimbul.faqueldb.data.Database
import com.cimbul.faqueldb.data.TransactionContext
import com.cimbul.faqueldb.ionElement
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.partiql.lang.eval.EvaluationException

class EvaluatorTest : DescribeSpec({
    val database = Database()
    val transaction = TransactionContext("", database)

    fun evaluate(statement: String, vararg parameters: IonElement): AnyElement {
        return transaction.inStatementContext(statement) { context ->
            val evaluator = Evaluator(context)
            evaluator.evaluate(statement, parameters.asList())
        }
    }

    describe("evaluate") {
        it("should evaluate expressions") {
            evaluate("1 + 1") shouldBe ionInt(2)
        }

        it("should interpolate parameters") {
            evaluate("2 + ?", ionInt(3)) shouldBe ionInt(5)
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

            evaluate(query) shouldBe ionElement("""
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
                    val result = evaluate("CREATE TABLE foo")

                    result.listValues.single().asStruct()["tableId"] shouldNotBe null
                }

                it("should throw an error if the table exists") {
                    evaluate("CREATE TABLE foo")

                    shouldThrow<EvaluationException> {
                        evaluate("CREATE TABLE foo")
                    }
                }

                it("should create an empty table") {
                    evaluate("CREATE TABLE foo")

                    val result = evaluate("SELECT * FROM foo")

                    result.listValues.shouldBeEmpty()
                }
            }

            describe("DROP TABLE") {
                it("should throw an error if the table does not exist") {
                    shouldThrow<EvaluationException> {
                        evaluate("DROP TABLE foo")
                    }
                }

                it("should return the table ID") {
                    val createResult = evaluate("CREATE TABLE foo")
                    val createResultStruct = createResult.listValues.single().asStruct()
                    val tableId = createResultStruct["tableId"]

                    val dropResult = evaluate("DROP TABLE foo")

                    val dropResultStruct = dropResult.listValues.single().asStruct()
                    dropResultStruct["tableId"] shouldBe tableId
                }

                it("should remove the table from the namespace") {
                    evaluate("CREATE TABLE foo")

                    evaluate("DROP TABLE foo")

                    shouldThrow<EvaluationException> {
                        evaluate("SELECT * FROM foo")
                    }
                }
            }

            describe("CREATE INDEX") {
                val tableResult = evaluate("CREATE TABLE foo")
                val tableId = tableResult.listValues.single().asStruct()["tableId"]

                it("should return the table ID") {
                    val indexResult = evaluate("CREATE INDEX ON foo (bar)")

                    indexResult.listValues.single().asStruct()["tableId"] shouldBe tableId
                }
            }
        }

        describe("DML") {
            evaluate("CREATE TABLE foo")

            describe("INSERT INTO table VALUE value") {
                it("should insert the record into the table") {
                    evaluate("INSERT INTO foo VALUE {'bar': 'quux'}")

                    evaluate("SELECT * FROM foo") shouldBe ionElement("""
                        [{bar: "quux"}]
                    """)
                }

                it("should preserve existing records") {
                    evaluate("INSERT INTO foo VALUE {'a': 1}")
                    evaluate("INSERT INTO foo VALUE {'a': 2}")

                    evaluate("SELECT * FROM foo") shouldBe ionElement("""
                       [{a: 1}, {a: 2}]
                    """)
                }

                it("should return the document ID of the record") {
                    val result = evaluate("INSERT INTO foo VALUE {'bar': 'quux'}")

                    result.listValues.single().asStruct()["documentId"] shouldNotBe null
                }
            }

            describe("INSERT INTO table values") {
                it("should insert the records into the table") {
                    evaluate("INSERT INTO foo << {'a': 1}, {'a': 2} >>")

                    evaluate("SELECT * FROM foo") shouldBe ionElement("""
                       [{a: 1}, {a: 2}]
                    """)
                }

                it("should return the document IDs for each record") {
                    val result = evaluate("INSERT INTO foo << {'a': 1}, {'a': 2} >>")

                    result.listValues shouldHaveSize 2
                    result.listValues.forAll { x ->
                        x.asStruct()["documentId"] shouldNotBe null
                    }
                }
            }
        }

        describe("BY clause") {
            evaluate("CREATE TABLE foo")
            val insertResult = evaluate("INSERT INTO foo << {'x': 0}, {'x': 1} >>")
            val documentIds = insertResult.listValues.map { it.asStruct()["documentId"] }

            it("should support projecting the document ID") {
                evaluate("SELECT docId, foo.x FROM foo BY docId") shouldBe ionListOf(
                    ionStructOf("docId" to documentIds[0], "x" to ionInt(0)),
                    ionStructOf("docId" to documentIds[1], "x" to ionInt(1)),
                )
            }

            it("should support filtering by the document ID") {
                val result = evaluate(
                    "SELECT foo.x FROM foo BY docId WHERE docId = ?",
                    documentIds[1]
                )

                result shouldBe ionListOf(
                    ionStructOf("x" to ionInt(1))
                )
            }

            it("should work in conjunction with table aliases") {
                val result = evaluate("SELECT docId, bar.x FROM foo AS bar BY docId")

                result shouldBe ionListOf(
                    ionStructOf("docId" to documentIds[0], "x" to ionInt(0)),
                    ionStructOf("docId" to documentIds[1], "x" to ionInt(1)),
                )
            }

            it("should work in conjunction with self joins") {
                val result = evaluate("""
                    SELECT a_id, a.x AS a_x, b_id, b.x AS b_x
                    FROM foo AS a BY a_id,
                         foo AS b BY b_id
                    WHERE a.x <= b.x
                """)

                result shouldBe ionListOf(
                    ionStructOf(
                        "a_id" to documentIds[0], "b_id" to documentIds[0],
                        "a_x" to ionInt(0),       "b_x" to ionInt(0),
                    ),
                    ionStructOf(
                        "a_id" to documentIds[0], "b_id" to documentIds[1],
                        "a_x" to ionInt(0),       "b_x" to ionInt(1),
                    ),
                    ionStructOf(
                        "a_id" to documentIds[1], "b_id" to documentIds[1],
                        "a_x" to ionInt(1),       "b_x" to ionInt(1),
                    ),
                )
            }
        }
    }
})
