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
    val dropped: Boolean = false,
    val indexes: List<Index> = listOf(),
    val latestCommittedRevisions: Map<String, CommittedRevision> = mapOf(),
    val latestRevisions: Map<String, Revision> = mapOf(),
    val committedRevisions: List<CommittedRevision> = listOf(),
    val uncommittedRevisions: List<UncommittedRevision> = listOf(),
) {
    fun toUserView(valueFactory: ExprValueFactory): ExprValue {
        return valueFactory.newBag(latestRevisions.values.asSequence().map { revision ->
            valueFactory.newFromIonElement(revision.data)
        })
    }

    fun toCommittedView(valueFactory: ExprValueFactory): ExprValue {
        return valueFactory.newBag(latestCommittedRevisions.values.asSequence().map { revision ->
            valueFactory.newFromIonElement(revision.ionElement)
        })
    }

    fun toMetadata(): IonElement = ionStructOf(
        "tableId" to ionString(id),
        "name" to ionString(name),
        "status" to ionString(if (dropped) "INACTIVE" else "ACTIVE"),
        "indexes" to ionListOf(indexes.map { it.toMetadata() })
    )

    fun withRevision(id: String, data: IonElement): Pair<Table, UncommittedRevision> {
        val latestVersion = latestRevisions[id]?.version ?: -1
        val revision = UncommittedRevision(id, latestVersion + 1, data)
        val table = copy(
            latestRevisions = latestRevisions + Pair(id, revision),
            uncommittedRevisions = uncommittedRevisions + revision,
        )
        return Pair(table, revision)
    }

    fun committed(commit: CommitInfo): Pair<Table, List<CommittedRevision>> {
        if (uncommittedRevisions.isEmpty()) {
            return Pair(this, listOf())
        }

        val committed = uncommittedRevisions.map { it.committed(commit) }
        val committedById = committed.associateBy { it.id }
        val table = copy(
            latestCommittedRevisions = latestCommittedRevisions + committedById,
            latestRevisions = latestRevisions + committedById,
            committedRevisions = committedRevisions + committed,
            uncommittedRevisions = listOf(),
        )
        return Pair(table, committed)
    }
}
