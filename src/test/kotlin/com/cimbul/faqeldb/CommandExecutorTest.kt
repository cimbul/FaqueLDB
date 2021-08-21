package com.cimbul.faqeldb

import com.amazon.ionelement.api.ionInt
import com.cimbul.faqeldb.session.ExecuteStatementRequest
import com.cimbul.faqeldb.session.SendCommandRequest
import com.cimbul.faqeldb.session.ValueHolder
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class CommandExecutorTest : DescribeSpec({
    val executor = CommandExecutor()

    describe("executeCommand") {
        it("should execute statements") {
            val request = SendCommandRequest(
                executeStatement = ExecuteStatementRequest(
                    transactionId = "foo",
                    statement = "? + 4",
                    parameters = listOf(ValueHolder(ionText = "3"))
                ),
            )

            val result = executor.executeCommand(request)

            val executeResult = result.executeStatement
            executeResult shouldNotBe null

            val resultValue = executeResult!!.firstPage.values.single().toIonElement()
            resultValue shouldBe ionInt(7)
        }
    }
})
