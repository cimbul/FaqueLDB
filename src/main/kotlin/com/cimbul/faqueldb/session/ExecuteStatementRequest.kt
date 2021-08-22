package com.cimbul.faqueldb.session

data class ExecuteStatementRequest(
    val transactionId: String,
    val statement: String,
    val parameters: List<ValueHolder>? = null,
)
