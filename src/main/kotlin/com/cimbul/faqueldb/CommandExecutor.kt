package com.cimbul.faqueldb

import com.cimbul.faqueldb.data.Database
import com.cimbul.faqueldb.data.TransactionContext
import com.cimbul.faqueldb.partiql.Evaluator
import com.cimbul.faqueldb.session.AbortTransactionResult
import com.cimbul.faqueldb.session.CommitTransactionRequest
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
    private var database = Database()
    private var transactions = mutableMapOf<String, TransactionContext>()

    private val sessionToken = "ba09bjmp32or8hvanv98fav"

    fun executeCommand(request: SendCommandRequest): SendCommandResult {
        return when {
            request.startSession != null -> SendCommandResult(
                startSession = StartSessionResult(sessionToken)
            )
            request.startTransaction != null -> SendCommandResult(
                startTransaction = startTransaction()
            )
            request.executeStatement != null -> SendCommandResult(
                executeStatement = executeStatement(request.executeStatement)
            )
            request.fetchPage != null -> SendCommandResult(
                fetchPage = FetchPageResult(Page(values = emptyList()))
            )
            request.commitTransaction != null -> SendCommandResult(
                commitTransaction = commitTransaction(request.commitTransaction)
            )
            request.abortTransaction != null -> SendCommandResult(
                abortTransaction = abortTransaction()
            )
            request.endSession != null -> SendCommandResult(
                endSession = endSession()
            )
            else -> TODO()
        }
    }

    private fun startTransaction(): StartTransactionResult {
        val transactionId = database.newId()
        val context = TransactionContext(transactionId, database)
        transactions[transactionId] = context
        return StartTransactionResult(transactionId)
    }

    private fun executeStatement(request: ExecuteStatementRequest): ExecuteStatementResult {
        val transaction = transactions[request.transactionId] ?:
            throw Exception("Invalid transaction ID")
        return transaction.inStatementContext(request.statement) { context ->
            val evaluator = Evaluator(context)
            val parameters = request.parameters?.map { it.toIonElement() } ?: emptyList()
            val value = evaluator.evaluate(request.statement, parameters)
            val page = Page(values = value.listValues.map(ValueHolder::binaryFrom))
            ExecuteStatementResult(firstPage = page)
        }
    }

    private fun commitTransaction(request: CommitTransactionRequest): CommitTransactionResult {
        // TODO: Check for commit conflicts
        val transaction = transactions[request.transactionId] ?:
            throw Exception("Invalid transaction ID")
        database = transaction.committed()
        // TODO: Check commit digest
        return CommitTransactionResult(request.transactionId, request.commitDigest)
    }

    private fun abortTransaction(): AbortTransactionResult {
        transactions.clear()
        return AbortTransactionResult()
    }

    private fun endSession(): EndSessionResult {
        abortTransaction()
        return EndSessionResult()
    }
}
