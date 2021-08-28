package com.cimbul.faqueldb.data

import com.amazon.ion.Timestamp
import com.amazon.ionelement.api.IonElement
import com.amazon.ionelement.api.ionInt
import com.amazon.ionelement.api.ionString
import com.amazon.ionelement.api.ionStructOf
import com.amazon.ionelement.api.ionTimestamp

data class RevisionMetadata(
    val id: String,
    val version: Long,
    val transactionTime: Timestamp,
    val transactionId: String,
) {
    val ionElement: IonElement = ionStructOf(
        "id" to ionString(id),
        "version" to ionInt(version),
        "txTime" to ionTimestamp(transactionTime),
        "txId" to ionString(transactionId)
    )

    val hash = Hash.of(ionElement)
}
