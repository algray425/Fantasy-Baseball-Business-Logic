package com.advanced_baseball_stats.v2.data

import com.advanced_baseball_stats.data.source.CioHttpClient
import com.advanced_baseball_stats.transformer.FanTraxToHolisticLeagueTransformer
import com.advanced_baseball_stats.v2.model.fantasy.HolisiticFantasyLeague
import com.advanced_baseball_stats.v2.model.fantrax.roster.FanTraxLeague

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

class FanTraxDataSource
{
    private val host                        = "https://www.fantrax.com/fxea/general/"
    private val holisticLeagueTransformer   = FanTraxToHolisticLeagueTransformer()

    private val rosterUrlExtension  = "getTeamRosters"
    private val matchupUrlExtension = "getLeagueInfo"

    suspend fun getFantasyTeamRosters(leagueId: String): HolisiticFantasyLeague?
    {
        val client = CioHttpClient.getClient()

        val rosterUrl = host + rosterUrlExtension

        val response = client.get(rosterUrl)
        {
            url {
                parameters.append("leagueId", leagueId)
                parameters.append("period", "6")
            }
            headers {
                append(HttpHeaders.Accept, "application/json")
                append(HttpHeaders.ContentType, "application/json")
            }
        }

        if (response.status.value in 200..299)
        {
            val json = Json {
                ignoreUnknownKeys = true
            }

            val fanTraxRosters: FanTraxLeague = json.decodeFromString<FanTraxLeague>(response.body())

            return holisticLeagueTransformer.transform(fanTraxRosters)
        }
        else
        {
            return null
        }
    }

//    suspend fun getFantasyTeamMatchup(leagueId: String)
//    {
//        val client = CioHttpClient.getClient()
//
//        val matchupUrl = host + matchupUrlExtension
//
//        val response = client.get(matchupUrl)
//        {
//            url {
//                parameters.append("leagueId", leagueId)
//            }
//            headers {
//                append(HttpHeaders.Accept, "application/json")
//                append(HttpHeaders.ContentType, "application/json")
//            }
//        }
//
//        if (response.status.value in 200..299)
//        {
//            val json = Json {
//                ignoreUnknownKeys = true
//            }
//
//            val matchups =
//        }
//    }
}