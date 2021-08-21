package com.cimbul.faqeldb

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import software.amazon.awssdk.services.qldbsession.model.ExecuteStatementRequest
import software.amazon.awssdk.services.qldbsession.model.ValueHolder

class CommandExecutorTest : DescribeSpec({
    val executor = CommandExecutor()

    describe("executeStatement") {
        it("should return a dummy value") {
            val request = ExecuteStatementRequest.builder().build {
                statement("1 + 1")
            }

            val result = executor.executeStatement(request)

            result.firstPage().values().single() shouldBe ValueHolder.builder().build {
                ionText("'Hello, world!'")
            }
        }
    }
})
