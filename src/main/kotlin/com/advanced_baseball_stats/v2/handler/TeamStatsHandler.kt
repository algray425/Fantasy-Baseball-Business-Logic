package com.advanced_baseball_stats.v2.handler

import com.advanced_baseball_stats.data.source.MlbApiSource
import com.advanced_baseball_stats.model.MlbApi.Schedule
import com.advanced_baseball_stats.model.common.Team
import com.advanced_baseball_stats.v2.helper.MlbIdToTeamAbbreviationConverter
import com.advanced_baseball_stats.v2.model.teams.hitting.TeamStatsHitting
import com.advanced_baseball_stats.v2.model.teams.overall.TeamSeasonStats
import com.advanced_baseball_stats.v2.model.teams.pitching.TeamStatsPitching
import com.advanced_baseball_stats.v2.model.teams.schedule.TeamGame
import com.advanced_baseball_stats.v2.model.teams.schedule.TeamGameSummary
import com.advanced_baseball_stats.v2.model.teams.schedule.TeamSchedule
import com.advanced_baseball_stats.v2.repository.stats.PlayerBattingSql
import com.advanced_baseball_stats.v2.repository.stats.PlayerPitchingSql
import com.advanced_baseball_stats.v2.repository.stats.TeamHittingSql
import com.advanced_baseball_stats.v2.repository.stats.TeamPitchingSql
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

class TeamStatsHandler
{
    private val mlbApiSource: MlbApiSource = MlbApiSource()

    fun getTeamsHittingStatsPerSeason(season: Int, sortBy: String): List<TeamStatsHitting>
    {
        val teamStatsHitting = TeamHittingSql.getAllTeamHittingStatsPerSeason(season, sortBy)

        return teamStatsHitting
    }

    fun getTeamsPitchingStatsPerSeason(season: Int): List<TeamStatsPitching>
    {
        val teamStatsPitching: List<TeamStatsPitching> = TeamPitchingSql.getAllTeamsPitchingStatsForSeason(season)

        return teamStatsPitching
    }

    fun getTeamSummary(team: String, season: Int): TeamSeasonStats?
    {
        val teamSummary = TeamHittingSql.getTeamSummary(team, season)

        if (teamSummary != null)
        {
            teamSummary.teamBatters             = PlayerBattingSql  .getBattersByTeam           (team, season)
            teamSummary.teamStartingPitchers    = PlayerPitchingSql .getStartingPitchersByTeam  (team, season)
            teamSummary.teamReliefPitchers      = PlayerPitchingSql .getReliefPitchersByTeam    (team, season)

            val schedule: Schedule?

            val currentDate     = LocalDate.now()
            val nextWeekDate    = currentDate.plusWeeks(1)

            runBlocking {
                schedule = mlbApiSource.getSchedulePerTeam(Team.valueOf(team), currentDate.toString(), nextWeekDate.toString())
            }

            if (schedule != null)
            {
                //get list of upcoming teams and starting pitchers
                //aggregate team and opposing pitcher grades

                val upcomingMatchups = mutableListOf<Pair<String, String>>()

                for (date in schedule.dates)
                {
                    for (game in date.games)
                    {
                        val homeTeamAbbr = MlbIdToTeamAbbreviationConverter.convertMlbIdToTeamAbbreviation(game.teams.homeTeam.team.id)
                        val awayTeamAbbr = MlbIdToTeamAbbreviationConverter.convertMlbIdToTeamAbbreviation(game.teams.awayTeam.team.id)

                        if (homeTeamAbbr.equals(team) && game.teams.awayTeam.probablePitcher != null)
                        {
                            val awayTeamPitcherId = game.teams.awayTeam.probablePitcher.id

                            upcomingMatchups.add(Pair(awayTeamAbbr, awayTeamPitcherId.toString()))
                        }
                        else if (homeTeamAbbr.equals(team))
                        {
                            upcomingMatchups.add(Pair(awayTeamAbbr, ""))
                        }
                        else if (awayTeamAbbr.equals(team) && game.teams.homeTeam.probablePitcher != null)
                        {
                            val homeTeamPitcherId = game.teams.homeTeam.probablePitcher.id

                            upcomingMatchups.add(Pair(homeTeamAbbr, homeTeamPitcherId.toString()))
                        }
                        else if (awayTeamAbbr.equals(team))
                        {
                            upcomingMatchups.add(Pair(homeTeamAbbr, ""))
                        }
                    }
                }

                val upcomingPitchersAndTeams = TeamPitchingSql.getUpcomingMatchupsAndGradesForTeam(upcomingMatchups, season)

                val upcomingPitchers = upcomingPitchersAndTeams.first
                val upcomingTeams    = upcomingPitchersAndTeams.second

                val games = mutableListOf<TeamGame>()

                for (matchup in upcomingMatchups)
                {
                    val matchupTeam = matchup.first
                    val pitcher     = matchup.second

                    val matchupTeamSummary  = upcomingTeams[matchupTeam] ?: TeamGameSummary("", 0.0, 0.0, 0.0, 0.0)
                    val pitcherSummary      = upcomingPitchers[pitcher]

                    games.add(TeamGame(matchupTeamSummary, pitcherSummary))
                }

                if (games.isNotEmpty())
                {
                    teamSummary.teamSchedule = TeamSchedule(games)
                }
            }
        }

        return teamSummary
    }
}