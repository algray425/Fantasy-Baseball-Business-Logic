package com.advanced_baseball_stats.handler.schedule

import com.advanced_baseball_stats.data.source.MlbApiSource
import com.advanced_baseball_stats.model.MlbApi.Schedule
import com.advanced_baseball_stats.model.common.Team
import com.advanced_baseball_stats.model.game.UpcomingGame
import com.advanced_baseball_stats.model.game.UpcomingPitcher
import com.advanced_baseball_stats.repository.PlayerBioSql
import com.advanced_baseball_stats.repository.PlayerPitchingSql
import com.advanced_baseball_stats.repository.TeamStatsSql
import kotlinx.coroutines.runBlocking

object ScheduleHandler {
    private val mlbTeamIdToRetroTeamId = mapOf(
        136 to "SEA",
        111 to "BOS",
        158 to "MIL",
        141 to "TOR",
        112 to "CHN",
        113 to "CIN",
        115 to "COL",
        140 to "TEX",
        109 to "ARI",
        144 to "ATL",
        117 to "HOU",
        142 to "MIN",
        133 to "OAK",
        118 to "KCA",
        110 to "BAL",
        147 to "NYA",
        120 to "WAS",
        108 to "ANA",
        145 to "CHA",
        146 to "MIA",
        139 to "TBA",
        143 to "PHI",
        116 to "DET",
        121 to "NYN",
        119 to "LAN",
        134 to "PIT",
        137 to "SFN",
        138 to "SLN",
        135 to "SDN",
        114 to "CLE"
    )

    private const val seasonStartDate = "2025-01-01"
    private const val seasonEndDate   = "2025-12-31"

    private val mlbApiSource = MlbApiSource()

    fun getUpcomingSchedule(team: String, startDate: String, endDate: String): List<UpcomingGame>
    {
        val upcomingGames = mutableListOf<UpcomingGame>()

        val convertedTeam = Team.valueOf(team)

        var teamSchedule: Schedule?

        runBlocking {
            teamSchedule = mlbApiSource.getSchedulePerTeam(convertedTeam, startDate, endDate)
        }

        if (teamSchedule != null)
        {
            for (date in teamSchedule!!.dates)
            {
                val gameDate = date.date

                for (game in date.games)
                {
                    val awayTeam = game.teams.awayTeam
                    val homeTeam = game.teams.homeTeam

                    val awayTeamId = awayTeam.team.id
                    val homeTeamId = homeTeam.team.id

                    val awayTeamRetroId = mlbTeamIdToRetroTeamId[awayTeamId]!!
                    val homeTeamRetroId = mlbTeamIdToRetroTeamId[homeTeamId]!!

                    val awayTeamPitcher = awayTeam.probablePitcher
                    val homeTeamPitcher = homeTeam.probablePitcher

                    var projectedAwayTeamPitcher: UpcomingPitcher? = null
                    var projectedHomeTeamPitcher: UpcomingPitcher? = null

                    if (awayTeamPitcher != null)
                    {
                        val awayTeamPitcherId = awayTeamPitcher.id

                        val awayTeamPitcherRetroId = PlayerBioSql.getRetroIdFromMlbId(awayTeamPitcherId)

                        val totalAwayTeamPitcherStats = PlayerPitchingSql.getTotalPlayerPitching(awayTeamPitcherRetroId, seasonStartDate, seasonEndDate)

                        projectedAwayTeamPitcher = UpcomingPitcher(awayTeamPitcher.fullName, totalAwayTeamPitcherStats.era, totalAwayTeamPitcherStats.whip, totalAwayTeamPitcherStats.homeRuns, totalAwayTeamPitcherStats.strikeouts)
                    }

                    if (homeTeamPitcher != null)
                    {
                        val homeTeamPitcherId = homeTeamPitcher.id

                        val homeTeamPitcherRetroId = PlayerBioSql.getRetroIdFromMlbId(homeTeamPitcherId)

                        val totalHomeTeamPitcherStats = PlayerPitchingSql.getTotalPlayerPitching(homeTeamPitcherRetroId, seasonStartDate, seasonEndDate)

                        projectedHomeTeamPitcher = UpcomingPitcher(homeTeamPitcher.fullName, totalHomeTeamPitcherStats.era, totalHomeTeamPitcherStats.whip, totalHomeTeamPitcherStats.homeRuns, totalHomeTeamPitcherStats.strikeouts)
                    }

                    val awayTeamTotalPitching = TeamStatsSql.getTeamPitching(awayTeamRetroId, seasonStartDate, seasonEndDate)!!
                    val homeTeamTotalPitching = TeamStatsSql.getTeamPitching(homeTeamRetroId, seasonStartDate, seasonEndDate)!!

                    val curGame = UpcomingGame(gameDate, homeTeamTotalPitching, awayTeamTotalPitching, projectedAwayTeamPitcher, projectedHomeTeamPitcher)

                    upcomingGames.add(curGame)
                }
            }
        }
        return upcomingGames
    }
}