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

class Insert(
    private val context: StatementContext,
    private val valueFactory: ExprValueFactory,
) : StoredProcedure {
    companion object {
        val signature = StoredProcedureSignature(internalName("insert"), 2)
    }

    override val signature = Insert.signature

    override fun call(session: EvaluationSession, args: List<ExprValue>): ExprValue {
        require(args.size == 2)
        val tableName = args[0].toIonElement(valueFactory).textValue
        val values = args[1].toIonElement(valueFactory).listValues

        val table = context.transaction.database[tableName] ?:
            throw EvaluationException("Table named '$tableName' does not exist",
                ErrorCode.EVALUATOR_BINDING_DOES_NOT_EXIST, internal = false)
        val valuesById = values.associateBy { context.transaction.database.newId() }

        for ((id, value) in valuesById) {
            context.addRevision(table.id, id, value)
        }

        return valueFactory.newBag(valuesById.keys.map { id ->
            valueFactory.newFromIonElement(ionStructOf(
                "documentId" to ionString(id)
            ))
        })
    }
}
