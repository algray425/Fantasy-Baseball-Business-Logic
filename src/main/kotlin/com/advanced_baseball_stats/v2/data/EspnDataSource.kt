package com.advanced_baseball_stats.v2.data

import com.advanced_baseball_stats.data.source.CioHttpClient
import com.advanced_baseball_stats.v2.model.espn.matchup.EspnSchedule
import com.advanced_baseball_stats.v2.model.espn.roster.EspnRosters
import io.ktor.client.call.*
import io.ktor.client.request.*

class EspnDataSource
{
    //https://lm-api-reads.fantasy.espn.com/apis/v3/games/flb/seasons/2025/segments/0/leagues/1166814511?view=mRoster
    private val host = "https://lm-api-reads.fantasy.espn.com/apis/v3/games/flb/seasons/2025/segments/0/leagues/"

    suspend fun getFantasyTeamRosters(leagueId: String): EspnRosters?
    {
        val client = CioHttpClient.getClient()

        val rosterUrl = host + leagueId

        val response = client.get(rosterUrl)
        {
            url {
                parameters.append("view", "mRoster")
            }
            cookie("espnAuth", "{\"swid\":\"{815D7769-011F-4749-A5FE-9F291697F5D6}\"}")
            cookie("espn_s2", "AECptpF7FrEKw0nGCX9CMGgxh+EAEZEb1FNc8cp3grOvYGc25fJrSLUfpB0KfB6KJb5uUQ2jR7nqzPpSk0eQf803Uwwlyr2JUVEwBzi5GRsFLFRazCVKzxoHVOKNRD7V1jicgwWFIYtzzE1CUmyU5FRSmX5FUu9Dfsr1xFx8fUksE9472LgaqpwM//WCu/XJKmoVJbbpKANrJ3VcU5MjipjP5BDTEV0glTPyesO0q/8ed4l8xGDmWFifo8RKbh9Jc+1V7i1q1/VngyjETWefv4bxTriJ/G99BBnbltBzFCp7QNFWM2j7NzZJWhXi9LO/EeQ=")
        }

        if (response.status.value in 200..299)
        {
            val espnRosters: EspnRosters = response.body()

            return espnRosters
        }
        else
        {
            return null
        }
    }

    //https://lm-api-reads.fantasy.espn.com/apis/v3/games/flb/seasons/2025/segments/0/leagues/1166814511?view=mMatchup&matchupPeriodId=1
    suspend fun getFantasyTeamMatchup(leagueId: String): EspnSchedule?
    {
        val client = CioHttpClient.getClient()

        val rosterUrl = host + leagueId

        val response = client.get(rosterUrl)
        {
            url {
                parameters.append("view", "mMatchup")
            }
            cookie("espnAuth", "{\"swid\":\"{815D7769-011F-4749-A5FE-9F291697F5D6}\"}")
            cookie("espn_s2", "AECptpF7FrEKw0nGCX9CMGgxh+EAEZEb1FNc8cp3grOvYGc25fJrSLUfpB0KfB6KJb5uUQ2jR7nqzPpSk0eQf803Uwwlyr2JUVEwBzi5GRsFLFRazCVKzxoHVOKNRD7V1jicgwWFIYtzzE1CUmyU5FRSmX5FUu9Dfsr1xFx8fUksE9472LgaqpwM//WCu/XJKmoVJbbpKANrJ3VcU5MjipjP5BDTEV0glTPyesO0q/8ed4l8xGDmWFifo8RKbh9Jc+1V7i1q1/VngyjETWefv4bxTriJ/G99BBnbltBzFCp7QNFWM2j7NzZJWhXi9LO/EeQ=")
        }

        if (response.status.value in 200..299)
        {
            val matchups: EspnSchedule = response.body()

            return matchups
        }
        else
        {
            return null
        }
    }

}