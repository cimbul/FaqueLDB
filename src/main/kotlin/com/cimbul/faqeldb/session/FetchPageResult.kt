package com.cimbul.faqeldb.session

data class FetchPageResult(
    val page: Page,
    val timingInformation: TimingInformation = TimingInformation(),
    val consumedIOs: IOUsage = IOUsage(),
)
