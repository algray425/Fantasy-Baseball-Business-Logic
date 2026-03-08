package com.advanced_baseball_stats.v2.handler

import com.advanced_baseball_stats.data.source.MlbApiSource
import com.advanced_baseball_stats.model.MlbApi.Schedule
import com.advanced_baseball_stats.model.common.Team
import com.advanced_baseball_stats.utility.converter.date.DateHelper
import com.advanced_baseball_stats.v2.data.EspnDataSource
import com.advanced_baseball_stats.v2.helper.MlbIdToTeamAbbreviationConverter
import com.advanced_baseball_stats.v2.model.batters.*
import com.advanced_baseball_stats.v2.model.common.GameStat
import com.advanced_baseball_stats.v2.model.espn.roster.EspnRosters
import com.advanced_baseball_stats.v2.model.game.HitterGame
import com.advanced_baseball_stats.v2.model.game.OpposingPitcherSummary
import com.advanced_baseball_stats.v2.model.game.PitcherGame
import com.advanced_baseball_stats.v2.model.pitchers.PitcherSeasonSummary
import com.advanced_baseball_stats.v2.model.pitchers.PitcherSummary
import com.advanced_baseball_stats.v2.model.pitchers.SeasonRankedReliefPitcher
import com.advanced_baseball_stats.v2.model.pitchers.SeasonRankedStartingPitcher
import com.advanced_baseball_stats.v2.repository.stats.PlayerBattingSql
import com.advanced_baseball_stats.v2.repository.stats.PlayerPitchingSql
import com.advanced_baseball_stats.v2.repository.stats.TeamHittingSql
import com.advanced_baseball_stats.v2.repository.stats.TeamPitchingSql

import kotlinx.coroutines.runBlocking

class PlayerStatsHandler
{
    val mlbApiSource    : MlbApiSource      = MlbApiSource()
    val espnDataSource  : EspnDataSource    = EspnDataSource()

    fun getRankedHittersBySeason(season: Int, sortBy: String, position: String, startDate: String, endDate: String, leagueTypeFilter: String, leagueIdFilter: String, limit: Int, page: Int): MutableList<SeasonRankedBatter>
    {
        val fullLeagueIds: MutableList<Int> = mutableListOf()

        if (leagueTypeFilter.isNotEmpty() && leagueIdFilter.isNotEmpty())
        {
            val espnRosters: EspnRosters?

            runBlocking {
                espnRosters = espnDataSource.getFantasyTeamRosters(leagueIdFilter)
            }

            if (espnRosters != null)
            {
                for (team in espnRosters.teams)
                {
                    val roster = team.roster

                    for (player in roster.playerEntry)
                    {
                        fullLeagueIds.add(player.espnId)
                    }
                }
            }
        }

        val positions = mutableListOf<String>()

        if (position.isNotEmpty())
        {
            positions.add(position)
        }

        if (position.equals("OF"))
        {
            positions.add("CF");
            positions.add("RF");
            positions.add("LF");
        }

        if (startDate.isNotEmpty() || endDate.isNotEmpty())
        {
            val convertedEndDate = if (endDate.isNotEmpty()) endDate else DateHelper.getCurrentDate()

            return PlayerBattingSql.getRankedBattersByStatInDateRange(season, sortBy, positions, startDate, convertedEndDate, fullLeagueIds, limit, page)
        }

        return PlayerBattingSql.getBattersRankedByStat(season, sortBy, positions, fullLeagueIds, limit, page)
    }

    fun getRankedStartingPitchersBySeason(season: Int, sortBy: String, startDate: String, endDate: String, leagueTypeFilter: String, leagueIdFilter: String, limit: Int, page: Int): MutableList<SeasonRankedStartingPitcher>
    {
        val fullLeagueIds: MutableList<Int> = mutableListOf()

        if (leagueTypeFilter.isNotEmpty() && leagueIdFilter.isNotEmpty())
        {
            val espnRosters: EspnRosters?

            runBlocking {
                espnRosters = espnDataSource.getFantasyTeamRosters(leagueIdFilter)
            }

            if (espnRosters != null)
            {
                for (team in espnRosters.teams)
                {
                    val roster = team.roster

                    for (player in roster.playerEntry)
                    {
                        fullLeagueIds.add(player.espnId)
                    }
                }
            }
        }

        if (startDate.isNotEmpty() || endDate.isNotEmpty())
        {
            val convertedEndDate = if (endDate.isNotEmpty()) endDate else DateHelper.getCurrentDate()

            return PlayerPitchingSql.getStartingPitcherRankedByStatInDateRange(season, sortBy, startDate, convertedEndDate, fullLeagueIds, limit, page)
        }

        return PlayerPitchingSql.getStartingPitchersRankedByStat(season, sortBy, fullLeagueIds, limit, page)
    }

    fun getRankedReliefPitchersBySeason(season: Int, sortBy: String, startDate: String, endDate: String, leagueTypeFilter: String, leagueIdFilter: String, limit: Int, page: Int): MutableList<SeasonRankedReliefPitcher>
    {
        val fullLeagueIds: MutableList<Int> = mutableListOf()

        if (leagueTypeFilter.isNotEmpty() && leagueIdFilter.isNotEmpty())
        {
            val espnRosters: EspnRosters?

            runBlocking {
                espnRosters = espnDataSource.getFantasyTeamRosters(leagueIdFilter)
            }

            if (espnRosters != null)
            {
                for (team in espnRosters.teams)
                {
                    val roster = team.roster

                    for (player in roster.playerEntry)
                    {
                        fullLeagueIds.add(player.espnId)
                    }
                }
            }
        }

        if (startDate.isNotEmpty() || endDate.isNotEmpty())
        {
            val convertedEndDate = if (endDate.isNotEmpty()) endDate else DateHelper.getCurrentDate()

            return PlayerPitchingSql.getReliefPitcherRankedByStatInDateRange(season, sortBy, startDate, convertedEndDate, fullLeagueIds, limit, page)
        }

        return PlayerPitchingSql.getReliefPitchersRankedByStat(season, sortBy, fullLeagueIds, limit, page)
    }

    fun getBatterProjections(sortBy: String, qualified: Boolean, position: String, leagueTypeFilter: String, leagueIdFilter: String, limit: Int, page: Int): MutableList<BatterProjection>
    {
        val fullLeagueIds = mutableListOf<Int>()

        if (leagueTypeFilter.isNotEmpty() && leagueIdFilter.isNotEmpty())
        {
            val espnRosters: EspnRosters?

            runBlocking {
                espnRosters = espnDataSource.getFantasyTeamRosters(leagueIdFilter)
            }

            if (espnRosters != null)
            {
                for (team in espnRosters.teams)
                {
                    val roster = team.roster

                    for (player in roster.playerEntry)
                    {
                        fullLeagueIds.add(player.espnId)
                    }
                }
            }
        }

        val positions = mutableListOf<String>()

        if (position.isNotEmpty())
        {
            positions.add(position)
        }

        if (position.equals("OF"))
        {
            positions.add("CF");
            positions.add("RF");
            positions.add("LF");
        }

        return PlayerBattingSql.getBatterProjections(sortBy, qualified, positions, fullLeagueIds, limit, page)
    }

    fun getHitterSummary(playerId: String): BatterSummary?
    {
        val playerSummary = PlayerBattingSql.getPlayerSummary(playerId)

        if (playerSummary != null)
        {
            val team = Team.valueOf(playerSummary.currentTeam)

            val schedule: Schedule?

            runBlocking {
                schedule = mlbApiSource.getSchedulePerTeam(team, "2026-03-25", "2026-03-29")
            }

            if (schedule != null)
            {
                for (date in schedule.dates)
                {
                    for (game in date.games)
                    {
                        val venue = game.venue.name

                        val homeTeamAbbr = MlbIdToTeamAbbreviationConverter.convertMlbIdToTeamAbbreviation(game.teams.homeTeam.team.id)
                        val awayTeamAbbr = MlbIdToTeamAbbreviationConverter.convertMlbIdToTeamAbbreviation(game.teams.awayTeam.team.id)

                        val opposingTeamAbbr = if (homeTeamAbbr.equals(playerSummary.currentTeam)) awayTeamAbbr else homeTeamAbbr

                        val opposingTeamPitchingGrades = TeamPitchingSql.getPitchingGradesByTeam(opposingTeamAbbr, 2026)

                        var opposingPitcherSummary: OpposingPitcherSummary? = null

                        if (homeTeamAbbr.equals(playerSummary.currentTeam) && game.teams.awayTeam.probablePitcher != null)
                        {
                            val probableOpposingPitcherId = game.teams.awayTeam.probablePitcher.id

                            opposingPitcherSummary = PlayerPitchingSql.getOpposingPitcherSummary(probableOpposingPitcherId.toString(), 2026)

                        }
                        else if (awayTeamAbbr.equals(playerSummary.currentTeam) && game.teams.homeTeam.probablePitcher != null)
                        {
                            val probableOpposingPitcherId = game.teams.homeTeam.probablePitcher.id

                            opposingPitcherSummary = PlayerPitchingSql.getOpposingPitcherSummary(probableOpposingPitcherId.toString(), 2026)
                        }

                        playerSummary.addGame(HitterGame(opposingTeamAbbr, venue, date.date, opposingTeamPitchingGrades, opposingPitcherSummary))
                    }
                }
            }
        }
        return playerSummary
    }

    fun getPitcherSummary(playerId: String): PitcherSummary?
    {
        val pitcherSummary = PlayerPitchingSql.getPlayerSummary(playerId)

        if (pitcherSummary != null && pitcherSummary.currentTeam.isNotEmpty())
        {
            val team = Team.valueOf(pitcherSummary.currentTeam)

            val schedule: Schedule?

            runBlocking {
                schedule = mlbApiSource.getSchedulePerTeam(team, "2026-03-25", "2026-03-29")
            }

            if (schedule != null)
            {
                for (date in schedule.dates)
                {
                    for (game in date.games)
                    {
                        val venue = game.venue.name

                        val homeTeamAbbr = MlbIdToTeamAbbreviationConverter.convertMlbIdToTeamAbbreviation(game.teams.homeTeam.team.id)
                        val awayTeamAbbr = MlbIdToTeamAbbreviationConverter.convertMlbIdToTeamAbbreviation(game.teams.awayTeam.team.id)

                        val opposingTeamAbbr = if (homeTeamAbbr.equals(pitcherSummary.currentTeam)) awayTeamAbbr else homeTeamAbbr

                        val opposingTeamHittingGrades = TeamHittingSql.getHittingGradesByTeam(opposingTeamAbbr, 2026)

                        pitcherSummary.addGame(PitcherGame(opposingTeamAbbr, venue, date.date, opposingTeamHittingGrades))
                    }
                }
            }
        }
        return pitcherSummary
    }

    fun getHitterSeasonSummaries(playerId: String, startSeason: String): MutableList<BatterSeasonSummary>
    {
        return PlayerBattingSql.getSeasonSummariesForHitter(playerId, startSeason)
    }

    fun getPitcherSeasonSummaries(playerId: String, startSeason: String): MutableList<PitcherSeasonSummary>
    {
        return PlayerPitchingSql.getSeasonSummariesForPitcher(playerId, startSeason)
    }

    fun getHittingStatPerGame(playerId: String, season: Int, stat: String): MutableList<GameStat>
    {
        return PlayerBattingSql.getHittingStatPerGame(playerId, season, stat)
    }

    fun getPitchingStatPerGame(playerId: String, season: Int, stat: String): MutableList<GameStat>
    {
        return PlayerPitchingSql.getPitchingStatPerGame(playerId, season, stat)
    }
}