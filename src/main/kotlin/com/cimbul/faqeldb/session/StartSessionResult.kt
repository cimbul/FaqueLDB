package com.cimbul.faqeldb.session

data class StartSessionResult(
    val sessionToken: String,
    val timingInformation: TimingInformation = TimingInformation(),
)
