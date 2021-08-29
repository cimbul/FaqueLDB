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

    describe("startTransaction") {
        it("should return a transaction ID") {
            val request = SendCommandRequest(
                startTransaction = Unit,
            )

            val result = executor.executeCommand(request)

            val startResult = result.startTransaction!!
            startResult.transactionId shouldNotBe null
        }
    }

    describe("executeCommand") {
        val transactionId = executor
            .executeCommand(SendCommandRequest(startTransaction = Unit))
            .startTransaction!!
            .transactionId

        it("should execute statements") {
            val request = SendCommandRequest(
                executeStatement = ExecuteStatementRequest(
                    transactionId = transactionId,
                    statement = "select ? + 4 as answer from << {} >>",
                    parameters = listOf(ValueHolder(ionText = "3"))
                ),
            )

            val result = executor.executeCommand(request)

            val executeResult = result.executeStatement!!
            val resultValue = executeResult.firstPage.values.single().toIonElement()
            resultValue shouldBe ionStructOf("answer" to ionInt(7))
        }
    }
})
