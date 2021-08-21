package com.cimbul.faqeldb

import com.amazon.ion.system.IonSystemBuilder
import org.partiql.lang.CompilerPipeline
import org.partiql.lang.eval.EvaluationSession
import org.partiql.lang.eval.ExprValueFactory
import software.amazon.awssdk.services.qldbsession.model.ExecuteStatementRequest
import software.amazon.awssdk.services.qldbsession.model.ExecuteStatementResult
import software.amazon.awssdk.services.qldbsession.model.Page
import software.amazon.awssdk.services.qldbsession.model.SendCommandRequest
import software.amazon.awssdk.services.qldbsession.model.SendCommandResponse

class CommandExecutor {
    private val ion = IonSystemBuilder.standard().build()
    private val valueFactory = ExprValueFactory.standard(ion)
    private val compiler = CompilerPipeline.standard(valueFactory)

    fun executeCommand(request: SendCommandRequest): SendCommandResponse {
        return SendCommandResponse.builder().build {
            executeStatement(executeStatement(request.executeStatement()))
        }
    }

    fun executeStatement(request: ExecuteStatementRequest): ExecuteStatementResult {
        val expression = compiler.compile(request.statement())
        val parameters = request.parameters()
            .map { it.ionValue(ion) }
            .map { valueFactory.newFromIonValue(it) }
        val value = expression.eval(EvaluationSession.build {
            parameters(parameters)
        })

        val page = Page.builder().build {
            nextPageToken(null)
            values(ionTextValue(value.ionValue))
        }

        return ExecuteStatementResult.builder().build {
            firstPage(page)
        }
    }
}
