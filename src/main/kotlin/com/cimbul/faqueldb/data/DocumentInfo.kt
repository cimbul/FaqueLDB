package com.cimbul.faqueldb.data

import com.amazon.ionelement.api.IonElement
import com.amazon.ionelement.api.ionInt
import com.amazon.ionelement.api.ionListOf
import com.amazon.ionelement.api.ionString
import com.amazon.ionelement.api.ionStructOf

data class DocumentInfo(
    val tableName: String,
    val tableId: String,
    val statements: List<Long>,
) {
    fun toIonElement(): IonElement = ionStructOf(
        "tableName" to ionString(tableName),
        "tableId" to ionString(tableId),
        "statements" to ionListOf(statements.map { ionInt(it) })
    )
}
