package com.cimbul.faqueldb.data

import com.amazon.ionelement.api.IonElement
import com.amazon.ionelement.api.ionString
import com.amazon.ionelement.api.ionStructOf

data class Index(
    val id: String,
    val expr: String,
) {
    fun toMetadata(): IonElement = ionStructOf(
        "indexId" to ionString(id),
        "expr" to ionString("[${expr}]"),
        "status" to ionString("ONLINE"),
    )
}
