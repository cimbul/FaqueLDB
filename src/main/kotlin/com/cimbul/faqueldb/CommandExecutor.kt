package com.cimbul.faqueldb

import com.cimbul.faqueldb.partiql.Evaluator
import com.cimbul.faqueldb.session.AbortTransactionResult
import com.cimbul.faqueldb.session.CommitTransactionResult
import com.cimbul.faqueldb.session.EndSessionResult
import com.cimbul.faqueldb.session.ExecuteStatementRequest
import com.cimbul.faqueldb.session.ExecuteStatementResult
import com.cimbul.faqueldb.session.FetchPageResult
import com.cimbul.faqueldb.session.Page
import com.cimbul.faqueldb.session.SendCommandRequest
import com.cimbul.faqueldb.session.SendCommandResult
import com.cimbul.faqueldb.session.StartSessionResult
import com.cimbul.faqueldb.session.StartTransactionResult
import com.cimbul.faqueldb.session.ValueHolder

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
