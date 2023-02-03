package com.cimbul.faqueldb.partiql.procedure

import com.amazon.ionelement.api.ionString
import com.amazon.ionelement.api.ionStructOf
import com.cimbul.faqueldb.data.StatementContext
import com.cimbul.faqueldb.data.Table
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

class CreateTable(
    private val context: StatementContext,
    private val valueFactory: ExprValueFactory
) : StoredProcedure {
    companion object {
        val signature = StoredProcedureSignature(internalName("create_table"), 1)
    }

    override val signature = CreateTable.signature

    override fun call(session: EvaluationSession, args: List<ExprValue>): ExprValue {
        require(args.size == 1)
        val name = args.single().toIonElement(valueFactory).textValue

        if (context.transaction.database[name] != null) {
            throw EvaluationException("Table with name '$name' already exists",
                ErrorCode.SEMANTIC_PROBLEM, internal = false)
        }

        val table = Table(context.transaction.database.newId(), name)
        context.transaction.database = context.transaction.database.withTable(table)

        return valueFactory.newBag(listOf(
            valueFactory.newFromIonElement(ionStructOf(
                "tableId" to ionString(table.id),
            ))
        ))
    }
}
