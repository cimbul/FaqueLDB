package com.cimbul.faqueldb.partiql.procedure

import com.amazon.ionelement.api.ionString
import com.amazon.ionelement.api.ionStructOf
import com.amazon.ionelement.api.toIonElement
import com.cimbul.faqueldb.data.StatementContext
import com.cimbul.faqueldb.partiql.internalName
import com.cimbul.faqueldb.partiql.newFromIonElement
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
        val tableName = args[0].ionValue.toIonElement().textValue
        val values = args[1].ionValue.toIonElement().listValues

        val table = context.transaction.database[tableName] ?:
            throw EvaluationException("Table named '$tableName' does not exist", internal = true)
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
