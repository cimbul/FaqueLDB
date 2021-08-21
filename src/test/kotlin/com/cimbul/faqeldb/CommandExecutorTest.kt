package com.cimbul.faqeldb

import com.amazon.ionelement.api.ionInt
import com.cimbul.faqeldb.session.ExecuteStatementRequest
import com.cimbul.faqeldb.session.ValueHolder
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class CommandExecutorTest : DescribeSpec({
    val executor = CommandExecutor()

    describe("executeStatement") {
        it("should evaluate expressions") {
            val request = ExecuteStatementRequest(
                transactionId = "foo",
                statement = "1 + 1",
            )

            val result = executor.executeStatement(request)

            result.firstPage.values.single().toIonElement() shouldBe ionInt(2)
        }

        it("should evaluate parameters") {
            val request = ExecuteStatementRequest(
                transactionId = "foo",
                statement = "2 + ?",
                parameters = listOf(ValueHolder(ionText = "3")),
            )

            val result = executor.executeStatement(request)

            result.firstPage.values.single().toIonElement() shouldBe ionInt(5)
        }

        it("should support queries on static data") {
            val request = ExecuteStatementRequest(
                transactionId = "foo",
                statement = """
                    SELECT e.name AS employeeName,
                           e.projects[0].name AS firstProjectName
                    FROM
                        {
                             'employeesNest': <<
                                 {
                                  'id': 3,
                                  'name': 'Bob Smith',
                                  'title': null,
                                  'projects': [ { 'name': 'AWS Redshift Spectrum querying' },
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
            )

            val result = executor.executeStatement(request)

            result.firstPage.values.single().toIonElement() shouldBe ionElement("""
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
    }
})
