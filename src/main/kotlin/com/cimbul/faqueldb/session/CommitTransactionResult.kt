package com.cimbul.faqueldb.session

data class CommitTransactionResult(
    val transactionId: String,
    val commitDigest: Bytes,
    val timingInformation: TimingInformation = TimingInformation(),
    val consumedIOs: IOUsage = IOUsage(),
)
