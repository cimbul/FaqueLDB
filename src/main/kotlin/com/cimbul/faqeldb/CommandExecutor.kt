package com.cimbul.faqeldb

import com.amazon.ion.system.IonSystemBuilder
import org.partiql.lang.CompilerPipeline
import org.partiql.lang.eval.EvaluationSession
import software.amazon.awssdk.services.qldbsession.model.ExecuteStatementRequest
import software.amazon.awssdk.services.qldbsession.model.ExecuteStatementResult
import software.amazon.awssdk.services.qldbsession.model.Page
import software.amazon.awssdk.services.qldbsession.model.SendCommandRequest
import software.amazon.awssdk.services.qldbsession.model.SendCommandResponse
import software.amazon.awssdk.services.qldbsession.model.ValueHolder
import java.io.StringWriter

class CommandExecutor {
    private val ion = IonSystemBuilder.standard().build()
    private val complier = CompilerPipeline.standard(ion)

    fun executeCommand(request: SendCommandRequest): SendCommandResponse {
        return SendCommandResponse.builder().build {
            executeStatement(executeStatement(request.executeStatement()))
        }
    }

    fun executeStatement(request: ExecuteStatementRequest): ExecuteStatementResult {
        val expression = complier.compile(request.statement())
        val value = expression.eval(EvaluationSession.standard())

        val out = StringWriter()
        val writer = ion.newTextWriter(out)
        value.ionValue.writeTo(writer)
        val valueText = out.toString()

        val valueHolder = ValueHolder.builder().build {
            ionText(valueText)
        }

        val page = Page.builder().build {
            nextPageToken(null)
            values(valueHolder)
        }

        return ExecuteStatementResult.builder().build {
            firstPage(page)
        }
    }
}
