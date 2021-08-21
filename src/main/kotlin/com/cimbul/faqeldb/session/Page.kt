package com.cimbul.faqeldb.session

data class Page(
    val values: List<ValueHolder>,
    val nextPageToken: String? = null,
)
