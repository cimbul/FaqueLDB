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

class DropTable(
    private val database: Database,
    private val valueFactory: ExprValueFactory,
) : StoredProcedure {
    companion object {
        val signature = StoredProcedureSignature(internalName("drop_table"), 1)
    }

    override val signature = DropTable.signature

    override fun call(session: EvaluationSession, args: List<ExprValue>): ExprValue {
        require(args.size == 1)
        val name = args.single().ionValue.toIonElement().textValue

        val table = database[name]
            ?: throw EvaluationException("Table name '$name' not found", internal = false)
        table.dropped = true

        return valueFactory.newBag(listOf(
            valueFactory.newFromIonElement(ionStructOf(
                "tableId" to ionString(table.id),
            ))
        ))
    }
}
