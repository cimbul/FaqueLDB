package com.cimbul.faqueldb.session

data class StartTransactionResult(
    val transactionId: String,
    val timingInformation: TimingInformation = TimingInformation(),
)
