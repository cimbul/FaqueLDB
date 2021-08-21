package com.cimbul.faqeldb

import com.cimbul.faqeldb.session.ExecuteStatementRequest
import com.cimbul.faqeldb.session.ExecuteStatementResult
import com.cimbul.faqeldb.session.Page
import com.cimbul.faqeldb.session.SendCommandRequest
import com.cimbul.faqeldb.session.SendCommandResult
import com.cimbul.faqeldb.session.ValueHolder

class CommandExecutor {
    private val evaluator = Evaluator()

    fun executeCommand(request: SendCommandRequest): SendCommandResult {
        return when {
            request.executeStatement != null -> SendCommandResult(
                executeStatement = executeStatement(request.executeStatement)
            )
            else -> TODO()
        }
    }

    private fun executeStatement(request: ExecuteStatementRequest): ExecuteStatementResult {
        val parameters = request.parameters?.map { it.toIonElement() } ?: emptyList()
        val value = evaluator.evaluate(request.statement, parameters)
        val page = Page(values = listOf(ValueHolder.textFrom(value)))
        return ExecuteStatementResult(firstPage = page)
    }
}
