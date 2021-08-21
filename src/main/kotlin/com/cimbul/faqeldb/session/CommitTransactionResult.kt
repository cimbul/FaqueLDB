package com.cimbul.faqeldb.session

data class CommitTransactionResult(
    val transactionId: String,
    val commitDigest: Bytes,
    val timingInformation: TimingInformation = TimingInformation(),
    val consumedIOs: IOUsage = IOUsage(),
)
