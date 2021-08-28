package com.cimbul.faqueldb.data

import com.amazon.ion.Timestamp
import com.amazon.ionelement.api.IonElement
import com.amazon.ionelement.api.ionString
import com.amazon.ionelement.api.ionStructOf
import com.amazon.ionelement.api.ionTimestamp

data class StatementInfo(
    val statement: String,
    val startTime: Timestamp,
) {
    // TODO: I have no idea how QLDB computes this. The examples provide no way to verify it.
    //   Ultimately, the statement and startTime (as well as this hash) are included in the
    //   transactionInfo hash, so its value doesn't actually matter.
    val digest = Hash.zero

    fun toIonElement(): IonElement = ionStructOf(
        "statement" to ionString(statement),
        "startTime" to ionTimestamp(startTime),
        "statementDigest" to digest.toIonElement()
    )
}
