package com.advanced_baseball_stats.data.source

import com.advanced_baseball_stats.model.MlbApi.Schedule
import com.advanced_baseball_stats.model.common.Team
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

class MlbApiSource
{
    private val host                = "https://statsapi.mlb.com/api/v1/"
    private val scheduleEndpoint    = "schedule"

    private val sportId     = "1"
    private val hydration   = "probablePitcher"

    private val teamAbbreviationToMlbId: Map<String, Int> = mapOf(
        "SEA" to 136,
        "BOS" to 111,
        "MIL" to 158,
        "TOR" to 141,
        "CHN" to 112,
        "CIN" to 113,
        "COL" to 115,
        "TEX" to 140,
        "ARI" to 109,
        "ATL" to 144,
        "HOU" to 117,
        "MIN" to 142,
        "OAK" to 133,
        "KCA" to 118,
        "BAL" to 110,
        "NYA" to 147,
        "WAS" to 120,
        "ANA" to 108,
        "CHA" to 145,
        "MIA" to 146,
        "TBA" to 139,
        "PHI" to 143,
        "DET" to 116,
        "NYN" to 121,
        "LAN" to 119,
        "PIT" to 134,
        "SFN" to 137,
        "SLN" to 138,
        "SDN" to 135,
        "CLE" to 114
    )

    //https://statsapi.mlb.com/api/v1/schedule?teamId=136&sportId=1&startDate=2025-07-21&endDate=2025-07-27&hydrate=probablePitcher
    suspend fun getSchedulePerTeam(team: Team, startDate: String, endDate: String): Schedule?
    {
        val teamMlbId = teamAbbreviationToMlbId[team.toString()]
        val client = CioHttpClient.getClient()

        val scheduleUrl = host + scheduleEndpoint

        //$host$scheduleEndpoint&team=$teamMlbId&&sportId=1&startDate=$startDate&endDate=$endDate&hydrate=probablePitcher
        val response = client.get(scheduleUrl)
        {
            url {
                parameters.append("teamId"      , teamMlbId.toString()  )
                parameters.append("sportId"     , sportId               )
                parameters.append("startDate"   , startDate             )
                parameters.append("endDate"     , endDate               )
                parameters.append("hydrate"     , hydration             )
            }
        }

        //client.close()

        if (response.status.value in 200..299)
        {
            val schedule: Schedule = response.body()

            return schedule
        }
        else
        {
            return null
        }
    }
}