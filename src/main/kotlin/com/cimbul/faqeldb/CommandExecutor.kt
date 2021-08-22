package com.cimbul.faqeldb

import com.cimbul.faqeldb.partiql.Evaluator
import com.cimbul.faqeldb.session.AbortTransactionResult
import com.cimbul.faqeldb.session.CommitTransactionResult
import com.cimbul.faqeldb.session.EndSessionResult
import com.cimbul.faqeldb.session.ExecuteStatementRequest
import com.cimbul.faqeldb.session.ExecuteStatementResult
import com.cimbul.faqeldb.session.FetchPageResult
import com.cimbul.faqeldb.session.Page
import com.cimbul.faqeldb.session.SendCommandRequest
import com.cimbul.faqeldb.session.SendCommandResult
import com.cimbul.faqeldb.session.StartSessionResult
import com.cimbul.faqeldb.session.StartTransactionResult
import com.cimbul.faqeldb.session.ValueHolder

class CommandExecutor {
    private val evaluator = Evaluator()

    private val sessionToken = "ba09bjmp32or8hvanv98fav"
    private val transactionId = "09b314a09fjpboan3h3i8va"

    fun executeCommand(request: SendCommandRequest): SendCommandResult {
        return when {
            request.startSession != null -> SendCommandResult(
                startSession = StartSessionResult(sessionToken)
            )
            request.startTransaction != null -> SendCommandResult(
                startTransaction = StartTransactionResult(transactionId)
            )
            request.executeStatement != null -> SendCommandResult(
                executeStatement = executeStatement(request.executeStatement)
            )
            request.fetchPage != null -> SendCommandResult(
                fetchPage = FetchPageResult(Page(values = emptyList()))
            )
            request.commitTransaction != null -> SendCommandResult(
                commitTransaction = CommitTransactionResult(
                    request.commitTransaction.transactionId,
                    request.commitTransaction.commitDigest,
                )
            )
            request.abortTransaction != null -> SendCommandResult(
                abortTransaction = AbortTransactionResult()
            )
            request.endSession != null -> SendCommandResult(
                endSession = EndSessionResult()
            )
            else -> TODO()
        }
    }

    private fun executeStatement(request: ExecuteStatementRequest): ExecuteStatementResult {
        val parameters = request.parameters?.map { it.toIonElement() } ?: emptyList()
        val value = evaluator.evaluate(request.statement, parameters)
        val page = Page(values = value.listValues.map(ValueHolder::binaryFrom))
        return ExecuteStatementResult(firstPage = page)
    }
}
