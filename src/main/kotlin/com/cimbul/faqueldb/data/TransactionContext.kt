package com.cimbul.faqueldb.data

import com.cimbul.faqueldb.toIonTimestamp

class TransactionContext(
    val id: String,
    var database: Database,
) {
    private val statements: MutableList<StatementContext> = mutableListOf()

    fun <T> inStatementContext(statement: String, f: (StatementContext) -> T): T {
        val startTime = database.clock.instant().toIonTimestamp()
        val statementContext = StatementContext(this, statement, startTime)
        val result = f(statementContext)
        statements.add(statementContext)
        return result
    }

    private fun createDocumentInfo(): Map<String, DocumentInfo> {
        val documentInfoById = mutableMapOf<String, DocumentInfo>()
        for ((index, statement) in statements.withIndex()) {
            for (statementRevision in statement.revisions) {
                val table = database.tables[statementRevision.tableId] ?:
                    throw Exception("Internal error committing transaction")
                val documentInfo = DocumentInfo(
                    tableName = table.name,
                    tableId = table.id,
                    statements = listOf(index.toLong())
                )
                documentInfoById.merge(statementRevision.documentId, documentInfo) { a, b ->
                    a.copy(statements = a.statements + b.statements)
                }
            }
        }
        return documentInfoById
    }

    private fun createTransactionInfo(): TransactionInfo = TransactionInfo(
        statements = statements.map { StatementInfo(it.statement, it.startTime) },
        documents = createDocumentInfo(),
    )

    fun committed(): Database = database.committed(id, createTransactionInfo())
}
