package com.cimbul.faqeldb

import com.amazon.ion.system.IonSystemBuilder
import com.cimbul.faqeldb.session.ExecuteStatementRequest
import com.cimbul.faqeldb.session.ExecuteStatementResult
import com.cimbul.faqeldb.session.Page
import com.cimbul.faqeldb.session.SendCommandRequest
import com.cimbul.faqeldb.session.SendCommandResult
import com.cimbul.faqeldb.session.ValueHolder
import org.partiql.lang.CompilerPipeline
import org.partiql.lang.eval.EvaluationSession
import org.partiql.lang.eval.ExprValueFactory

class CommandExecutor {
    private val ion = IonSystemBuilder.standard().build()
    private val valueFactory = ExprValueFactory.standard(ion)
    private val compiler = CompilerPipeline.standard(valueFactory)

    fun executeCommand(request: SendCommandRequest): SendCommandResult {
        return when {
            request.executeStatement != null -> SendCommandResult(
                executeStatement = executeStatement(request.executeStatement)
            )
            else -> TODO()
        }
    }

    fun executeStatement(request: ExecuteStatementRequest): ExecuteStatementResult {
        val expression = compiler.compile(request.statement)
        val parameters = (request.parameters ?: emptyList())
            .map { it.toIonValue(ion) }
            .map { valueFactory.newFromIonValue(it) }
        val value = expression.eval(EvaluationSession.build {
            parameters(parameters)
        })

        val page = Page(values = listOf(ValueHolder.textFrom(value.ionValue)))
        return ExecuteStatementResult(firstPage = page)
    }
}
