package com.cimbul.faqeldb.session

data class FetchPageRequest(
    val transactionId: String,
    val nextPageToken: String,
)
