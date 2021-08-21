package com.cimbul.faqeldb

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import software.amazon.awssdk.services.qldbsession.model.ExecuteStatementRequest

class CommandExecutorTest : DescribeSpec({
    val executor = CommandExecutor()

    describe("executeStatement") {
        it("should evaluate expressions") {
            val request = ExecuteStatementRequest.builder().build {
                statement("1 + 1")
            }

            val result = executor.executeStatement(request)

            result.firstPage().values().single() shouldBe ionTextValue("2")
        }

        it("should evaluate parameters") {
            val request = ExecuteStatementRequest.builder().build {
                statement("2 + ?")
                parameters(ionTextValue("3"))
            }

            val result = executor.executeStatement(request)

            result.firstPage().values().single() shouldBe ionTextValue("5")
        }
    }
})
