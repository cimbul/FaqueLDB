package com.cimbul.faqeldb.session

data class StartTransactionResult(
    val transactionId: String,
    val timingInformation: TimingInformation = TimingInformation(),
)
