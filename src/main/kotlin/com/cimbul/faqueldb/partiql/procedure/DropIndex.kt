package com.cimbul.faqueldb.partiql.procedure

import com.amazon.ionelement.api.ionString
import com.amazon.ionelement.api.ionStructOf
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

class DropIndex(
    private val context: StatementContext,
    private val valueFactory: ExprValueFactory
) : StoredProcedure {
    companion object {
        val signature = StoredProcedureSignature(internalName("drop_index"), 2)
    }

    override val signature = DropIndex.signature

    override fun call(session: EvaluationSession, args: List<ExprValue>): ExprValue {
        require(args.size == 2)
        val tableName = args[0].toIonElement(valueFactory).textValue
        val indexId = args[1].toIonElement(valueFactory).textValue

        val table = context.transaction.database[tableName] ?:
            throw EvaluationException("Table '$tableName' does not exist",
                ErrorCode.EVALUATOR_BINDING_DOES_NOT_EXIST, internal = false)

        val index = table.indexes.find { it.id == indexId } ?:
            throw EvaluationException("Index '$indexId' does not exist on table '$tableName'",
                ErrorCode.EVALUATOR_BINDING_DOES_NOT_EXIST, internal = false)

        val indexes = table.indexes - index
        context.transaction.database = context.transaction.database
            .withTable(table.copy(indexes = indexes))

        return valueFactory.newBag(listOf(
            valueFactory.newFromIonElement(ionStructOf(
                "tableId" to ionString(table.id),
            ))
        ))
    }
}
