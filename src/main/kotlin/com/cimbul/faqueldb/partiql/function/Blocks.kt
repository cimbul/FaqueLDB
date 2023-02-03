package com.cimbul.faqueldb.partiql.function

import com.amazon.ionelement.api.ionString
import com.amazon.ionelement.api.ionStructOf
import com.cimbul.faqueldb.data.StatementContext
import com.cimbul.faqueldb.partiql.internalName
import com.cimbul.faqueldb.partiql.newFromIonElement
import com.cimbul.faqueldb.partiql.toIonElement
import org.partiql.lang.errors.ErrorCode
import org.partiql.lang.eval.EvaluationException
import org.partiql.lang.eval.EvaluationSession
import org.partiql.lang.eval.ExprFunction
import org.partiql.lang.eval.ExprValue
import org.partiql.lang.eval.ExprValueFactory
import org.partiql.lang.types.FunctionSignature
import org.partiql.lang.types.StaticType

class Blocks(
    private val context: StatementContext,
    private val valueFactory: ExprValueFactory
) : ExprFunction {
    val name = internalName("blocks")
    override val signature = FunctionSignature(name, listOf(StaticType.TEXT), StaticType.LIST)

    override fun callWithRequired(session: EvaluationSession, required: List<ExprValue>): ExprValue {
        require(required.size == 1) { "$name expected 1 argument, got ${required.size}" }
        val tableName = required.single().toIonElement(valueFactory).textValue

        val table = context.transaction.database[tableName] ?:
            throw EvaluationException("No table named $tableName",
                ErrorCode.EVALUATOR_BINDING_DOES_NOT_EXIST, internal = false)
        val blocks = table.latestRevisions.values
            .asSequence()
            .map { revision ->
                valueFactory.newFromIonElement(ionStructOf(
                    "data" to revision.data,
                    "metadata" to ionStructOf(
                        "id" to ionString(revision.id),
                    ),
                ))
            }

        return valueFactory.newList(blocks)
    }
}
