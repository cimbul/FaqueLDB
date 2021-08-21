package com.cimbul.faqeldb

import com.amazon.ionelement.api.ionInt
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class EvaluatorTest : DescribeSpec({
    val evaluator = Evaluator()

    describe("evaluate") {
        it("should evaluate expressions") {
            evaluator.evaluate("1 + 1", emptyList()) shouldBe ionInt(2)
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

            evaluator.evaluate(query, emptyList()) shouldBe ionElement("""
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

        it("should support CREATE TABLE") {
            val query = "CREATE TABLE foo"

            val result = evaluator.evaluate(query, emptyList())

            val resultStruct = result.listValues.single().asStruct()
            resultStruct["tableId"] shouldNotBe null
        }
    }
})
