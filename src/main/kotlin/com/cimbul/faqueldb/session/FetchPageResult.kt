package com.cimbul.faqueldb.session

data class FetchPageResult(
    val page: Page,
    val timingInformation: TimingInformation = TimingInformation(),
    val consumedIOs: IOUsage = IOUsage(),
)
