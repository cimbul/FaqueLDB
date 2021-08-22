package com.cimbul.faqueldb.session

data class FetchPageRequest(
    val transactionId: String,
    val nextPageToken: String,
)
