package com.cimbul.faqueldb.data

import com.amazon.ionelement.api.IonElement
import com.amazon.ionelement.api.ionListOf
import com.amazon.ionelement.api.ionString
import com.amazon.ionelement.api.ionStructOf
import com.cimbul.faqueldb.partiql.newFromIonElement
import org.partiql.lang.eval.ExprValue
import org.partiql.lang.eval.ExprValueFactory

data class Table(
    val id: String,
    val name: String,
    var dropped: Boolean = false,
    val indexes: MutableList<Index> = mutableListOf(),
    val documents: MutableMap<String, IonElement> = mutableMapOf(),
) {
    fun toExprValue(valueFactory: ExprValueFactory): ExprValue {
        return valueFactory.newBag(documents.values.asSequence().map { document ->
            valueFactory.newFromIonElement(document)
        })
    }

    fun toMetadata(): IonElement = ionStructOf(
        "tableId" to ionString(id),
        "name" to ionString(name),
        "status" to ionString(if (dropped) "INACTIVE" else "ACTIVE"),
        "indexes" to ionListOf(indexes.map { it.toMetadata() })
    )
}
