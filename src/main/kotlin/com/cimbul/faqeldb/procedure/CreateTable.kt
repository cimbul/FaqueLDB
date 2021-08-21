package com.cimbul.faqeldb.procedure

import com.amazon.ionelement.api.ionString
import com.amazon.ionelement.api.ionStructOf
import com.amazon.ionelement.api.toIonElement
import com.cimbul.faqeldb.newFromIonElement
import org.partiql.lang.eval.EvaluationSession
import org.partiql.lang.eval.ExprValue
import org.partiql.lang.eval.ExprValueFactory
import org.partiql.lang.eval.builtins.storedprocedure.StoredProcedure
import org.partiql.lang.eval.builtins.storedprocedure.StoredProcedureSignature

class CreateTable(private val valueFactory: ExprValueFactory) : StoredProcedure {
    companion object {
        val signature = StoredProcedureSignature(procedureNamePrefix + "create_table", 1)
    }

    override val signature = CreateTable.signature

    override fun call(session: EvaluationSession, args: List<ExprValue>): ExprValue {
        require(args.size == 1)
        val nameArg = args.single()

        val name = nameArg.ionValue.toIonElement().textValue
        val tableId = name + "_a09u31ojaoF"

        return valueFactory.newBag(listOf(
            valueFactory.newFromIonElement(ionStructOf(
                "tableId" to ionString(tableId),
            ))
        ))
    }
}
