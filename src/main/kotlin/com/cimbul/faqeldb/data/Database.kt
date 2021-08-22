package com.cimbul.faqeldb.data

import com.amazon.ionelement.api.IonElement
import com.amazon.ionelement.api.ionListOf
import com.amazon.ionelement.api.ionString
import com.amazon.ionelement.api.ionStructOf
import com.cimbul.faqeldb.nextUUID
import com.cimbul.faqeldb.partiql.newFromIonElement
import org.partiql.lang.eval.BindingCase
import org.partiql.lang.eval.BindingName
import org.partiql.lang.eval.ExprValue
import org.partiql.lang.eval.ExprValueFactory
import org.unbrokendome.base62.Base62
import kotlin.random.Random

data class Database(
    val tables: MutableList<Table> = mutableListOf(),
    val random: Random = Random.Default,
) {
    operator fun get(binding: BindingName): Table? {
        return tables.find {
            !it.dropped && binding.isEquivalentTo(it.name)
        }
    }

    operator fun get(name: String): Table? {
        return get(BindingName(name, BindingCase.INSENSITIVE))
    }

    fun getBinding(binding: BindingName, valueFactory: ExprValueFactory): ExprValue? {
        if (binding.isEquivalentTo("information_schema")) {
            return valueFactory.newFromIonElement(ionStructOf(
                "user_tables" to userTables()
            ))
        }

        return this[binding]?.toExprValue(valueFactory)
    }

    private fun userTables(): IonElement {
        return ionListOf(tables.map { table ->
            ionStructOf(
                "tableId" to ionString(table.id),
                "name" to ionString(table.name),
                "status" to ionString(if (table.dropped) "INACTIVE" else "ACTIVE"),
                "indexes" to ionListOf(table.indexes.map { index ->
                    ionStructOf(
                        "indexId" to ionString(index.id),
                        "expr" to ionString("[${index.expr}]"),
                        "status" to ionString("ONLINE"),
                    )
                })
            )
        })
    }

    fun newId(): String {
        return Base62.encodeUUID(random.nextUUID())
    }
}
