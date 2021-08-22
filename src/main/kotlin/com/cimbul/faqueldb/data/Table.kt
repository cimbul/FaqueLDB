package com.cimbul.faqueldb.data

import com.amazon.ionelement.api.IonElement
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
}
