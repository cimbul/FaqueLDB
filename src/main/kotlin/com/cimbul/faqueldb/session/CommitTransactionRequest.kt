package com.cimbul.faqueldb.session

data class CommitTransactionRequest(
    val transactionId: String,
    val commitDigest: Bytes,
)
