package com.cimbul.faqueldb.data

import com.amazon.ionelement.api.IonElement
import com.amazon.ionelement.api.ionListOf
import com.amazon.ionelement.api.ionStructOf
import com.cimbul.faqueldb.nextUUID
import com.cimbul.faqueldb.partiql.newFromIonElement
import com.cimbul.faqueldb.toIonTimestamp
import org.partiql.lang.eval.BindingCase
import org.partiql.lang.eval.BindingName
import org.partiql.lang.eval.ExprValue
import org.partiql.lang.eval.ExprValueFactory
import org.unbrokendome.base62.Base62
import java.time.Clock
import kotlin.random.Random

data class Database(
    val journal: List<Block> = listOf(),
    val tables: Map<String, Table> = mapOf(),
    val random: Random = Random.Default,
    val clock: Clock = Clock.systemUTC(),
) {
    operator fun get(binding: BindingName): Table? {
        return tables.values.find {
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

    private fun userTables(): IonElement = ionListOf(tables.values.map { it.toMetadata() })

    fun newId(): String {
        return Base62.encodeUUID(random.nextUUID())
    }

    fun withTable(table: Table): Database =
        copy(tables = tables + Pair(table.id, table))

    fun committed(transactionId: String, transactionInfo: TransactionInfo): Database {
        val commitTime = clock.instant().toIonTimestamp()

        val lastBlock = journal.lastOrNull()
        val address = lastBlock?.address
            ?.let { BlockAddress(it.strandID, it.sequenceNumber + 1) }
            ?: BlockAddress(newId(), 0)
        val previousHash = lastBlock?.hash ?: Hash.zero

        val commitInfo = CommitInfo(transactionId, commitTime, address)
        val newTables = mutableListOf<Table>()
        val revisions = mutableListOf<CommittedRevision>()
        for (table in tables.values) {
            val (newTable, tableRevisions) = table.committed(commitInfo)
            newTables += newTable
            revisions += tableRevisions
        }

        val block = Block(
            address = address,
            transactionId = transactionId,
            timestamp = commitTime,
            previousBlockHash = previousHash,
            transactionInfo = transactionInfo,
            revisions = revisions,
        )

        return copy(
            journal = journal + block,
            tables = newTables.associateBy { it.id },
        )
    }
}
