package com.cimbul.faqeldb

import software.amazon.awssdk.services.qldbsession.model.ExecuteStatementRequest
import software.amazon.awssdk.services.qldbsession.model.ExecuteStatementResult
import software.amazon.awssdk.services.qldbsession.model.Page
import software.amazon.awssdk.services.qldbsession.model.SendCommandRequest
import software.amazon.awssdk.services.qldbsession.model.SendCommandResponse
import software.amazon.awssdk.services.qldbsession.model.ValueHolder

class CommandExecutor {
    fun executeCommand(request: SendCommandRequest): SendCommandResponse {
        return SendCommandResponse.builder().build {
            executeStatement(executeStatement(request.executeStatement()))
        }
    }

    fun executeStatement(request: ExecuteStatementRequest): ExecuteStatementResult {
        val value = ValueHolder.builder().build {
            ionText("'Hello, world!'")
        }

        val page = Page.builder().build {
            nextPageToken(null)
            values(value)
        }

        return ExecuteStatementResult.builder().build {
            firstPage(page)
        }
    }
}
