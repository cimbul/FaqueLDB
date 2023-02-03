package com.cimbul.faqueldb.partiql.procedure

import com.amazon.ionelement.api.ionString
import com.amazon.ionelement.api.ionStructOf
import com.cimbul.faqueldb.data.Index
import com.cimbul.faqueldb.data.StatementContext
import com.cimbul.faqueldb.partiql.internalName
import com.cimbul.faqueldb.partiql.newFromIonElement
import com.cimbul.faqueldb.partiql.toIonElement
import org.partiql.lang.errors.ErrorCode
import org.partiql.lang.eval.EvaluationException
import org.partiql.lang.eval.EvaluationSession
import org.partiql.lang.eval.ExprValue
import org.partiql.lang.eval.ExprValueFactory
import org.partiql.lang.eval.builtins.storedprocedure.StoredProcedure
import org.partiql.lang.eval.builtins.storedprocedure.StoredProcedureSignature

class CreateIndex(
    private val context: StatementContext,
    private val valueFactory: ExprValueFactory
) : StoredProcedure {
    companion object {
        val signature = StoredProcedureSignature(internalName("create_index"), 2)
    }

    override val signature = CreateIndex.signature

    override fun call(session: EvaluationSession, args: List<ExprValue>): ExprValue {
        require(args.size == 2)
        val tableName = args[0].toIonElement(valueFactory).textValue
        val indexFields = args[1].toIonElement(valueFactory)
        require(indexFields.listValues.size == 1) { "Indexes only supported on exactly one field" }
        val indexField = indexFields.listValues.single().textValue

        val table = context.transaction.database[tableName] ?:
            throw EvaluationException("Table '$tableName' does not exist",
                ErrorCode.EVALUATOR_BINDING_DOES_NOT_EXIST, internal = false)

        if (table.indexes.any { it.expr == indexField }) {
            throw EvaluationException("Index on field $indexField already exists",
                ErrorCode.SEMANTIC_PROBLEM, internal = false)
        }

        val indexes = table.indexes + Index(context.transaction.database.newId(), indexField)
        context.transaction.database = context.transaction.database
            .withTable(table.copy(indexes = indexes))

        return valueFactory.newBag(listOf(
            valueFactory.newFromIonElement(ionStructOf(
                "tableId" to ionString(table.id),
            ))
        ))
    }
}
