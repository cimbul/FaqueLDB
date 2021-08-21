package com.cimbul.faqeldb.data

import com.cimbul.faqeldb.nextUUID
import org.partiql.lang.eval.BindingCase
import org.partiql.lang.eval.BindingName
import org.partiql.lang.util.isBindingNameEquivalent
import org.unbrokendome.base62.Base62
import kotlin.random.Random

data class Database(
    val tables: MutableList<Table> = mutableListOf(),
    val random: Random = Random.Default,
) {
    operator fun get(binding: BindingName): Table? {
        return tables.find {
            !it.dropped && it.name.isBindingNameEquivalent(binding.name, binding.bindingCase)
        }
    }

    operator fun get(name: String): Table? {
        return get(BindingName(name, BindingCase.INSENSITIVE))
    }

    fun newId(): String {
        return Base62.encodeUUID(random.nextUUID())
    }
}
