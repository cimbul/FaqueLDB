package com.cimbul.faqueldb.partiql.procedure

import com.amazon.ionelement.api.ionString
import com.amazon.ionelement.api.ionStructOf
import com.amazon.ionelement.api.toIonElement
import com.cimbul.faqueldb.data.Database
import com.cimbul.faqueldb.partiql.internalName
import com.cimbul.faqueldb.partiql.newFromIonElement
import org.partiql.lang.eval.EvaluationException
import org.partiql.lang.eval.EvaluationSession
import org.partiql.lang.eval.ExprValue
import org.partiql.lang.eval.ExprValueFactory
import org.partiql.lang.eval.builtins.storedprocedure.StoredProcedure
import org.partiql.lang.eval.builtins.storedprocedure.StoredProcedureSignature

class Insert(
    private val database: Database,
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

        val table = database[tableName] ?:
            throw EvaluationException("Table named '$tableName' does not exist", internal = true)
        val valuesById = values.associateBy { database.newId() }
        table.documents.putAll(valuesById)

        return valueFactory.newBag(valuesById.keys.map { id ->
            valueFactory.newFromIonElement(ionStructOf(
                "documentId" to ionString(id)
            ))
        })
    }
}
