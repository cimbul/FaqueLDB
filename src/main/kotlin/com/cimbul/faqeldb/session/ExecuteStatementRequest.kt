package com.cimbul.faqeldb.session

data class ExecuteStatementRequest(
    val transactionId: String,
    val statement: String,
    val parameters: List<ValueHolder>?,
)
