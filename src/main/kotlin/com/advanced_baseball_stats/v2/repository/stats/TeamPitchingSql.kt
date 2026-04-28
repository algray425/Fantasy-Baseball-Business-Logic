package com.advanced_baseball_stats.v2.repository.stats

import com.advanced_baseball_stats.v2.model.teams.pitching.TeamStatsPitching
import com.advanced_baseball_stats.v2.model.teams.schedule.TeamGame
import com.advanced_baseball_stats.v2.model.teams.schedule.TeamGameSummary
import com.advanced_baseball_stats.v2.model.teams.schedule.TeamPitcher
import com.advanced_baseball_stats.v2.repository.stats.tables.*
import org.ktorm.dsl.*

object TeamPitchingSql
{
    fun getPitchingGradesByTeam(team: String, season: Int): Double?
    {
        DatabaseConnection.database.from(SeasonGradesTeamPitchingTable)
            .select(SeasonGradesTeamPitchingTable.percentileOverall)
            .where{(SeasonGradesTeamPitchingTable.team eq team) and (SeasonGradesTeamPitchingTable.season eq season)}
            .forEachIndexed { index,teamRow ->
                if (index == 0)
                {
                    return teamRow[SeasonGradesTeamPitchingTable.percentileOverall]
                }
            }
        return null
    }

    fun getPitchingGradesByPitcher(pitchers: Set<String>, season: Int): Map<String, Pair<Double,Double>>
    {
        val teamToMatchupGrades = mutableMapOf<String, Pair<Double, Double>>()

        DatabaseConnection.database.from(BiosTable)
            .leftJoin(SeasonGradesTeamPitchingTable, on = (SeasonGradesTeamPitchingTable.team eq BiosTable.currentTeam) and (SeasonGradesTeamPitchingTable.season eq season))
            .leftJoin(SeasonGradesStartingPitchersTable, on = (SeasonGradesStartingPitchersTable.playerId eq BiosTable.playerId) and (SeasonGradesStartingPitchersTable.season eq season))
            .select(SeasonGradesTeamPitchingTable.team, SeasonGradesTeamPitchingTable.percentileOverall, SeasonGradesStartingPitchersTable.percentileOverall)
            .where{BiosTable.playerId inList pitchers}
            .forEach { teamRow ->
                val team                = teamRow[SeasonGradesTeamPitchingTable.team] ?: ""
                val teamPercentile      = teamRow[SeasonGradesTeamPitchingTable.percentileOverall] ?: 0.0
                val pitcherPercentile   = teamRow[SeasonGradesStartingPitchersTable.percentileOverall] ?: 50.0

                if (team.isNotEmpty())
                {
                    val inversePitcherPercentile = 100.0 - pitcherPercentile
                    teamToMatchupGrades[team] = Pair(teamPercentile, inversePitcherPercentile)
                }
            }
        return teamToMatchupGrades
    }

    fun getAllTeamsPitchingStatsForSeason(season: Int): List<TeamStatsPitching>
    {
        val teamStatsPitching = mutableListOf<TeamStatsPitching>()

        DatabaseConnection.database.from(TeamsStatsPitchingPerSeasonTable)
            .select()
            .where { TeamsStatsPitchingPerSeasonTable.season eq season }
            .forEach { teamRow ->
                teamStatsPitching.add(
                    TeamStatsPitching(
                    team = teamRow[TeamsStatsPitchingPerSeasonTable.team] ?: "",
                    season = teamRow[TeamsStatsPitchingPerSeasonTable.season] ?: 0,
                    ipOuts = teamRow[TeamsStatsPitchingPerSeasonTable.ipOuts] ?: 0,
                    battersFaced = teamRow[TeamsStatsPitchingPerSeasonTable.battersFaced] ?: 0,
                    hits = teamRow[TeamsStatsPitchingPerSeasonTable.hits] ?: 0,
                    singles = teamRow[TeamsStatsPitchingPerSeasonTable.singles] ?: 0,
                    doubles = teamRow[TeamsStatsPitchingPerSeasonTable.doubles] ?: 0,
                    triples = teamRow[TeamsStatsPitchingPerSeasonTable.triples] ?: 0,
                    homeRuns = teamRow[TeamsStatsPitchingPerSeasonTable.homeRuns] ?: 0,
                    runs = teamRow[TeamsStatsPitchingPerSeasonTable.runs] ?: 0,
                    earnedRuns = teamRow[TeamsStatsPitchingPerSeasonTable.earnedRuns] ?: 0,
                    walks = teamRow[TeamsStatsPitchingPerSeasonTable.walks] ?: 0,
                    intentionalWalks = teamRow[TeamsStatsPitchingPerSeasonTable.intentionalWalks] ?: 0,
                    strikeOuts = teamRow[TeamsStatsPitchingPerSeasonTable.strikeOuts] ?: 0,
                    hitByPitch = teamRow[TeamsStatsPitchingPerSeasonTable.hitByPitch] ?: 0,
                    wildPitches = teamRow[TeamsStatsPitchingPerSeasonTable.wildPitches] ?: 0,
                    balks = teamRow[TeamsStatsPitchingPerSeasonTable.balks] ?: 0,
                    sacHits = teamRow[TeamsStatsPitchingPerSeasonTable.sacHits] ?: 0,
                    sacFlies = teamRow[TeamsStatsPitchingPerSeasonTable.sacFlies] ?: 0,
                    stolenBases = teamRow[TeamsStatsPitchingPerSeasonTable.stolenBases] ?: 0,
                    caughtStealing = teamRow[TeamsStatsPitchingPerSeasonTable.caughtStealing] ?: 0,
                    passedBalls = teamRow[TeamsStatsPitchingPerSeasonTable.passedBalls] ?: 0,
                    era = teamRow[TeamsStatsPitchingPerSeasonTable.era] ?: 0.0,
                    whip = teamRow[TeamsStatsPitchingPerSeasonTable.whip] ?: 0.0,
                    ksPerNine = teamRow[TeamsStatsPitchingPerSeasonTable.ksPerNine] ?: 0.0,
                    rbisAgainst = teamRow[TeamsStatsPitchingPerSeasonTable.rbisAgainst] ?: 0,
                    onBasePercentageAgainst = teamRow[TeamsStatsPitchingPerSeasonTable.onBasePercentageAgainst] ?: 0.0
                )
                )
            }
        return teamStatsPitching
    }

    fun getTeamPitchingStats(team: String, season: Int): TeamStatsPitching?
    {
        return DatabaseConnection.database.from(TeamsStatsPitchingPerSeasonTable)
            .select()
            .where {
                (TeamsStatsPitchingPerSeasonTable.team eq team) and (TeamsStatsPitchingPerSeasonTable.season eq season)
            }
            .map { row ->
                TeamStatsPitching(
                    team = row[TeamsStatsPitchingPerSeasonTable.team] ?: "",
                    season = row[TeamsStatsPitchingPerSeasonTable.season] ?: 0,
                    ipOuts = row[TeamsStatsPitchingPerSeasonTable.ipOuts] ?: 0,
                    battersFaced = row[TeamsStatsPitchingPerSeasonTable.battersFaced] ?: 0,
                    hits = row[TeamsStatsPitchingPerSeasonTable.hits] ?: 0,
                    singles = row[TeamsStatsPitchingPerSeasonTable.singles] ?: 0,
                    doubles = row[TeamsStatsPitchingPerSeasonTable.doubles] ?: 0,
                    triples = row[TeamsStatsPitchingPerSeasonTable.triples] ?: 0,
                    homeRuns = row[TeamsStatsPitchingPerSeasonTable.homeRuns] ?: 0,
                    runs = row[TeamsStatsPitchingPerSeasonTable.runs] ?: 0,
                    earnedRuns = row[TeamsStatsPitchingPerSeasonTable.earnedRuns] ?: 0,
                    walks = row[TeamsStatsPitchingPerSeasonTable.walks] ?: 0,
                    intentionalWalks = row[TeamsStatsPitchingPerSeasonTable.intentionalWalks] ?: 0,
                    strikeOuts = row[TeamsStatsPitchingPerSeasonTable.strikeOuts] ?: 0,
                    hitByPitch = row[TeamsStatsPitchingPerSeasonTable.hitByPitch] ?: 0,
                    wildPitches = row[TeamsStatsPitchingPerSeasonTable.wildPitches] ?: 0,
                    balks = row[TeamsStatsPitchingPerSeasonTable.balks] ?: 0,
                    sacHits = row[TeamsStatsPitchingPerSeasonTable.sacHits] ?: 0,
                    sacFlies = row[TeamsStatsPitchingPerSeasonTable.sacFlies] ?: 0,
                    stolenBases = row[TeamsStatsPitchingPerSeasonTable.stolenBases] ?: 0,
                    caughtStealing = row[TeamsStatsPitchingPerSeasonTable.caughtStealing] ?: 0,
                    passedBalls = row[TeamsStatsPitchingPerSeasonTable.passedBalls] ?: 0,
                    era = row[TeamsStatsPitchingPerSeasonTable.era] ?: 0.0,
                    whip = row[TeamsStatsPitchingPerSeasonTable.whip] ?: 0.0,
                    ksPerNine = row[TeamsStatsPitchingPerSeasonTable.ksPerNine] ?: 0.0,
                    rbisAgainst = row[TeamsStatsPitchingPerSeasonTable.rbisAgainst] ?: 0,
                    onBasePercentageAgainst = row[TeamsStatsPitchingPerSeasonTable.onBasePercentageAgainst] ?: 0.0
                )
            }.firstOrNull()
    }

    fun getUpcomingMatchupsAndGradesForTeam(matchups: List<Pair<String,String>>, season: Int): Pair<Map<String, TeamPitcher>, Map<String, TeamGameSummary>>
    {
        val upcomingTeams       = mutableMapOf<String, TeamGameSummary> ()
        val upcomingPitchers    = mutableMapOf<String, TeamPitcher>     ()

        val teams       = matchups.toMap().keys     .toList()
        val pitchers    = matchups.map { it.second }.filter { it.isNotEmpty() }

        DatabaseConnection.database.from(SeasonGradesTeamPitchingTable)
            .innerJoin(SeasonGradesTeamHittingTable, on = SeasonGradesTeamHittingTable.team eq SeasonGradesTeamPitchingTable.team)
            .leftJoin(BiosTable, (SeasonGradesTeamPitchingTable.team eq BiosTable.currentTeam) and (BiosTable.playerId inList pitchers))
            .leftJoin(SeasonGradesStartingPitchersTable, (SeasonGradesStartingPitchersTable.playerId eq BiosTable.playerId) and
                    (SeasonGradesStartingPitchersTable.season eq season))
            .select(
                SeasonGradesTeamPitchingTable.team,
                SeasonGradesTeamHittingTable.percentileOverall,
                SeasonGradesTeamHittingTable.percentileStartingPitchers,
                SeasonGradesTeamHittingTable.percentileReliefPitchers,
                SeasonGradesTeamPitchingTable.percentileOverall,
                BiosTable.playerId,
                BiosTable.firstName,
                BiosTable.lastName,
                SeasonGradesStartingPitchersTable.percentileOverall
            )
            .where {
                ((SeasonGradesTeamPitchingTable.team inList teams) and (SeasonGradesTeamHittingTable.season eq season) and
                        (SeasonGradesTeamPitchingTable.season eq season)) }
            .forEach { matchupRow ->
                val pitcherId           = matchupRow[BiosTable.playerId]
                val firstName           = matchupRow[BiosTable.firstName]
                val lastName            = matchupRow[BiosTable.lastName ]
                val percentileOverall   = matchupRow[SeasonGradesStartingPitchersTable.percentileOverall] ?: 50.0

                if (pitcherId != null && firstName != null && lastName != null && !upcomingPitchers.containsKey(pitcherId))
                {
                    val concatenatedName = "$firstName $lastName"

                    upcomingPitchers[pitcherId] = TeamPitcher(concatenatedName, percentileOverall)
                }

                val team                        = matchupRow[SeasonGradesTeamPitchingTable.team                       ] ?: ""
                val teamPercentileHitting       = matchupRow[SeasonGradesTeamHittingTable.percentileOverall           ] ?: 0.0
                val startingPitchersPercentile  = matchupRow[SeasonGradesTeamHittingTable.percentileStartingPitchers  ] ?: 0.0
                val reliefPitchersPercentile    = matchupRow[SeasonGradesTeamHittingTable.percentileReliefPitchers    ] ?: 0.0
                val overallPitchingPercentile   = matchupRow[SeasonGradesTeamPitchingTable.percentileOverall          ] ?: 0.0

                if (!upcomingTeams.containsKey(team))
                {
                    upcomingTeams[team] = TeamGameSummary(team, teamPercentileHitting, startingPitchersPercentile, reliefPitchersPercentile, overallPitchingPercentile)
                }
            }
        return Pair(upcomingPitchers, upcomingTeams)
    }
}