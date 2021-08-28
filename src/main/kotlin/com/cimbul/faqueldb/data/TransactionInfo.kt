package com.cimbul.faqueldb.data

import com.amazon.ionelement.api.IonElement
import com.amazon.ionelement.api.ionListOf
import com.amazon.ionelement.api.ionStructOf

data class TransactionInfo(
    val statements: List<StatementInfo>,
    val documents: Map<String, DocumentInfo>
) {
    val ionElement: IonElement = ionStructOf(
        "statements" to ionListOf(statements.map { it.toIonElement() }),
        "documents" to ionStructOf(
            *documents
                .map { it.key to it.value.toIonElement() }
                .toTypedArray()
        )
    )

    val hash = Hash.of(ionElement)
}
