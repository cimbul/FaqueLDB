package com.cimbul.faqueldb.session

data class Page(
    val values: List<ValueHolder>,
    val nextPageToken: String? = null,
)
