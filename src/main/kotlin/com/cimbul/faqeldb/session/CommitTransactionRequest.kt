package com.cimbul.faqeldb.session

data class CommitTransactionRequest(
    val transactionId: String,
    val commitDigest: Bytes,
)
