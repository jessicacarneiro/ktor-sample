package com.example.plugins

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*

@OptIn(InternalAPI::class)
fun Application.configureRouting() {

    val client = HttpClient(CIO) {
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
    }

    routing {
        get("/token") {
            val tokenResponse = client.post("https://accounts.spotify.com/api/token") {
                headers {
                    append(HttpHeaders.Authorization, "Basic ${generateKey()}")
                    append(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded)
                }
                body = FormDataContent(Parameters.build {
                    append("client_id", System.getenv("CLIENT_ID"))
                    append("client_secret", System.getenv("CLIENT_SECRET"))
                    append("grant_type", "client_credentials")
                })
            }

            call.respondText(tokenResponse.body(), ContentType.Application.Json)
        }
    }
}


fun generateKey(): String {
    val clientId = System.getenv("CLIENT_ID")
    val clientSecret = System.getenv("CLIENT_SECRET")

    return "$clientId:$clientSecret".encodeBase64()
}