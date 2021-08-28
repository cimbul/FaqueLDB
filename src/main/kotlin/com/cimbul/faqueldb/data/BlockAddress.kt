package com.cimbul.faqueldb.data

import com.amazon.ionelement.api.IonElement
import com.amazon.ionelement.api.ionInt
import com.amazon.ionelement.api.ionString
import com.amazon.ionelement.api.ionStructOf

data class BlockAddress(val strandID: String, val sequenceNumber: Long) {
    val ionElement: IonElement = ionStructOf(
        "strandId" to ionString(strandID),
        "sequenceNo" to ionInt(sequenceNumber),
    )
}
