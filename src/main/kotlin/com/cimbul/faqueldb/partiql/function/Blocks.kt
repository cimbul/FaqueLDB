package com.cimbul.faqueldb.partiql.function

import com.amazon.ionelement.api.ionString
import com.amazon.ionelement.api.ionStructOf
import com.amazon.ionelement.api.toIonElement
import com.cimbul.faqueldb.data.StatementContext
import com.cimbul.faqueldb.partiql.internalName
import com.cimbul.faqueldb.partiql.newFromIonElement
import org.partiql.lang.eval.Environment
import org.partiql.lang.eval.EvaluationException
import org.partiql.lang.eval.ExprFunction
import org.partiql.lang.eval.ExprValue
import org.partiql.lang.eval.ExprValueFactory

class Blocks(
    private val context: StatementContext,
    private val valueFactory: ExprValueFactory
) : ExprFunction {
    override val name = internalName("blocks")

    override fun call(env: Environment, args: List<ExprValue>): ExprValue {
        require(args.size == 1) { "$name expected 1 argument, got ${args.size}" }
        val tableName = args.single().ionValue.toIonElement().textValue

        val table = context.transaction.database[tableName] ?:
            throw EvaluationException("No table named $tableName", internal = false)
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
