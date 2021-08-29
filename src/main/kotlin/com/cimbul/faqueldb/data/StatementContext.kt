package com.cimbul.faqueldb.data

import com.amazon.ion.Timestamp
import com.amazon.ionelement.api.IonElement
import org.partiql.lang.eval.EvaluationException

class StatementContext(
    val transaction: TransactionContext,
    val statement: String,
    val startTime: Timestamp,
) {
    private val _revisions: MutableList<StatementRevision> =
        mutableListOf()
    val revisions: List<StatementRevision>
        get() = _revisions.toList()

    fun addRevision(tableId: String, documentId: String, data: IonElement) {
        val table = transaction.database.tables[tableId] ?:
            throw EvaluationException("No table with ID '$tableId'", internal = true)
        val (newTable, revision) = table.withRevision(documentId, data)
        transaction.database = transaction.database.withTable(newTable)
        _revisions.add(StatementRevision(tableId, revision.id))
    }
}
