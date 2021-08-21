package com.cimbul.faqeldb.data

import com.amazon.ionelement.api.IonElement

data class Table(
    val id: String,
    val name: String,
    var dropped: Boolean = false,
    val indexes: MutableList<Index> = mutableListOf(),
    val records: MutableList<IonElement> = mutableListOf(),
)
