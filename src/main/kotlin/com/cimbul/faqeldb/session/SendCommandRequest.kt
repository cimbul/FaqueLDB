package com.cimbul.faqeldb.session

data class SendCommandRequest(
    val startSession: StartSessionRequest? = null,
    val sessionToken: String? = null,
    val startTransaction: Unit? = null,
    val executeStatement: ExecuteStatementRequest? = null,
    val fetchPage: FetchPageRequest? = null,
    val commitTransaction: CommitTransactionRequest? = null,
    val abortTransaction: Unit? = null,
    val endSession: Unit? = null,
)
