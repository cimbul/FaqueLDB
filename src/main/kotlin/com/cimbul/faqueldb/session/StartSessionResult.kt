package com.cimbul.faqueldb.session

data class StartSessionResult(
    val sessionToken: String,
    val timingInformation: TimingInformation = TimingInformation(),
)
