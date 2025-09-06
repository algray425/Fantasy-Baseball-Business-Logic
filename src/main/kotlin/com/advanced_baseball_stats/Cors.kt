package com.advanced_baseball_stats

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

fun Application.configureCors()
{
    install(CORS)
    {
        allowHost("localhost:9393")
        allowMethod(HttpMethod.Get)
        allowHeader(HttpHeaders.ContentType)
        allowCredentials = true
    }
}