package com.cimbul.faqueldb.session

data class ExecuteStatementResult(
    val firstPage: Page,
    val timingInformation: TimingInformation = TimingInformation(),
    val consumedIOs: IOUsage = IOUsage(),
)
