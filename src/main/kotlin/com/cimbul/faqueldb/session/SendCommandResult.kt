package com.cimbul.faqueldb.session

data class SendCommandResult(
    val startSession: StartSessionResult? = null,
    val startTransaction: StartTransactionResult? = null,
    val executeStatement: ExecuteStatementResult? = null,
    val fetchPage: FetchPageResult? = null,
    val commitTransaction: CommitTransactionResult? = null,
    val abortTransaction: AbortTransactionResult? = null,
    val endSession: EndSessionResult? = null,
)
