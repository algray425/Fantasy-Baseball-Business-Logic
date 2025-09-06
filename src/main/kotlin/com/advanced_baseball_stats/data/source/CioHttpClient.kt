package com.advanced_baseball_stats.data.source

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object CioHttpClient {
    private val client = HttpClient(CIO)
    {
        install(ContentNegotiation)
        {
            json(
                Json
                {
                    prettyPrint         = true
                    isLenient           = true
                    ignoreUnknownKeys   = true
                }
            )
        }
    };

    fun getClient(): HttpClient
    {
        return client
    }
}