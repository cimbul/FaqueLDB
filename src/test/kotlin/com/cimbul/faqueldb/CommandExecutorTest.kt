package com.cimbul.faqueldb

import com.amazon.ionelement.api.ionInt
import com.amazon.ionelement.api.ionStructOf
import com.cimbul.faqueldb.session.ExecuteStatementRequest
import com.cimbul.faqueldb.session.SendCommandRequest
import com.cimbul.faqueldb.session.ValueHolder
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
                    statement = "select ? + 4 as answer from << {} >>",
                    parameters = listOf(ValueHolder(ionText = "3"))
                ),
            )

            val result = executor.executeCommand(request)

            val executeResult = result.executeStatement
            executeResult shouldNotBe null

            val resultValue = executeResult!!.firstPage.values.single().toIonElement()
            resultValue shouldBe ionStructOf("answer" to ionInt(7))
        }
    }
})
