package com.cimbul.faqueldb

import com.cimbul.faqueldb.session.SendCommandRequest
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.server.Netty
import org.http4k.server.asServer

class Application {
    private val executor = CommandExecutor()
    private val mapper = ObjectMapper()
        .setPropertyNamingStrategy(PropertyNamingStrategies.UpperCamelCaseStrategy())
        .setSerializationInclusion(Include.NON_NULL)
        .registerKotlinModule()

    fun handle(request: Request): Response {
        val sendCommandRequest = mapper.readValue<SendCommandRequest>(request.body.stream)
        val sendCommandResponse = executor.executeCommand(sendCommandRequest)
        return Response(Status.OK).body(mapper.writeValueAsString(sendCommandResponse))
    }
}

fun main() {
    val app = Application()
    app::handle.asServer(Netty(8000)).start().block()
}
