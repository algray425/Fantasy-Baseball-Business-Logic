package com.advanced_baseball_stats

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.Assert.assertEquals
import org.junit.Test

class ApplicationTest {
    private val expectedPlayersResponse: String = "[{\"name\":\"Julio Rodriguez\",\"team\":\"Seattle Mariners\",\"position\":\"Center Field\",\"number\":44,\"age\":24,\"height\":\"6'3''\",\"weight\":228},{\"name\":\"Juan Soto\",\"team\":\"New York Mets\",\"position\":\"Outfielder\",\"number\":22,\"age\":26,\"height\":\"6'2''\",\"weight\":224}]"

    @Test
    fun testPlayers() = testApplication {
        application {
            module()
        }
        val response = client.get("/players")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedPlayersResponse, response.bodyAsText())
    }
}