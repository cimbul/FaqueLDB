package com.cimbul.faqeldb.session

data class CommitTransactionResult(
    val transactionId: String,
    val commitDigest: ByteArray,
    val timingInformation: TimingInformation = TimingInformation(),
    val consumedIOs: IOUsage = IOUsage(),
)
