package com.advanced_baseball_stats.v2.handler

import com.advanced_baseball_stats.v2.data.EspnDataSource
import com.advanced_baseball_stats.v2.model.batters.Fantasy.FantasyPlayerSummary
import com.advanced_baseball_stats.v2.model.batters.Fantasy.FantasyTeam
import com.advanced_baseball_stats.v2.model.batters.Fantasy.FantasyTeamSummary
import com.advanced_baseball_stats.v2.model.espn.matchup.EspnSchedule
import com.advanced_baseball_stats.v2.model.espn.roster.EspnRosters
import com.advanced_baseball_stats.v2.model.espn.roster.EspnTeam
import com.advanced_baseball_stats.v2.repository.stats.PlayerBattingSql
import com.advanced_baseball_stats.v2.repository.stats.PlayerPitchingSql
import com.advanced_baseball_stats.v2.repository.users.FantasyTeamsSql
import com.advanced_baseball_stats.v2.transformer.EspnToHolisticFantasyScheduleTransformer
import kotlinx.coroutines.runBlocking
import kotlin.time.measureTimedValue

class FantasyTeamsHandler
{
    private val espnDataSource                          : EspnDataSource                            = EspnDataSource()
    private val espnToHolisticFantasyScheduleTransformer: EspnToHolisticFantasyScheduleTransformer  = EspnToHolisticFantasyScheduleTransformer()

    fun getFantasyTeams(userId: String): MutableList<FantasyTeam>
    {
        return FantasyTeamsSql.getFantasyTeamsForUser(userId)
    }

    fun getFantasyTeamSummary(userId: String, leagueType: String, leagueId: String, teamId: String, weekNumber: Int): FantasyTeamSummary
    {
        val espnRosters: EspnRosters?

        runBlocking {
            espnRosters = espnDataSource.getFantasyTeamRosters(leagueId)
        }

        val fantasySchedule: EspnSchedule?

        runBlocking {
            fantasySchedule = espnDataSource.getFantasyTeamMatchup(leagueId)
        }

        if (fantasySchedule != null && espnRosters != null)
        {
            val weekNumbers: Set<Int> = setOf(weekNumber, weekNumber + 1)

            val fantasyTeamMatchups = espnToHolisticFantasyScheduleTransformer.transform(fantasySchedule, teamId.toInt(), weekNumbers)

            val curMatchup      = fantasyTeamMatchups[0]
            val upcomingMatchup = fantasyTeamMatchups[1]

            val opposingTeamUpcomingMatchup = if (upcomingMatchup.awayTeamSummary.teamId == teamId.toInt()) upcomingMatchup.homeTeamSummary else upcomingMatchup.awayTeamSummary

            val opposingTeamUpcomingMatchupPlayers = espnRosters.teams.find { it.id == opposingTeamUpcomingMatchup.teamId }

            val opposingTeamPlayerIds = mutableListOf<Int>()

            if (opposingTeamUpcomingMatchupPlayers != null)
            {
                for (player in opposingTeamUpcomingMatchupPlayers.roster.playerEntry)
                {
                    opposingTeamPlayerIds.add(player.espnId)
                }
            }

            val opposingPlayerSummaries = PlayerBattingSql.getEspnFantasyPlayerSummaries(opposingTeamPlayerIds, 2025)

            val userTeam: EspnTeam? = espnRosters.teams.find { it.id == teamId.toInt() }

            var playerSummaries: MutableList<FantasyPlayerSummary> = mutableListOf()

            if (userTeam != null)
            {
                val playerIds: MutableList<Int> = mutableListOf()

                for (player in userTeam.roster.playerEntry)
                {
                    playerIds.add(player.espnId)
                }

                val(playerSummariesResponse, playerSummariesDuration) = measureTimedValue {
                    PlayerBattingSql.getEspnFantasyPlayerSummaries(playerIds, 2025)
                }

                playerSummaries = playerSummariesResponse

                println("Player summaries duration: $playerSummariesDuration")
            }

            val fullLeagueIds: MutableList<Int> = mutableListOf()

            for (team in espnRosters.teams)
            {
                val roster = team.roster

                for (player in roster.playerEntry)
                {
                    fullLeagueIds.add(player.espnId)
                }
            }

            //get best available hitters
            val (bestAvailableHitters, hitterDuration) = measureTimedValue {
                PlayerBattingSql.getBestAvailableBattersOverallFromEspn(fullLeagueIds, 2025)
            }

            println("Hitter rank time duration: $hitterDuration")


            val (bestAvailableStartingPitchers, startingPitcherDuration) = measureTimedValue {
                PlayerPitchingSql.getBestAvailableStartingPitchersFromEspn(fullLeagueIds, 2025)
            }

            println("Starting Pitcher rank time duration: $startingPitcherDuration")

            //get best available relief pitchers
            val (bestAvailableReliefPitchers, reliefPitcherDuration) = measureTimedValue {
                PlayerPitchingSql.getBestAvailableReliefPitchersFromEspn(fullLeagueIds, 2025)
            }

            println("Relief Pitcher rank time duration: $reliefPitcherDuration")

            return FantasyTeamSummary(playerSummaries, curMatchup, opposingPlayerSummaries, bestAvailableHitters, bestAvailableStartingPitchers, bestAvailableReliefPitchers)
        }

        return FantasyTeamSummary(mutableListOf(), null, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf())
    }
}