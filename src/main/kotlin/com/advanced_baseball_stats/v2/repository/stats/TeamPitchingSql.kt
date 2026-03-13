package com.advanced_baseball_stats.v2.repository.stats

import com.advanced_baseball_stats.v2.model.teams.hitting.pitching.TeamStatsPitching
import com.advanced_baseball_stats.v2.repository.stats.tables.SeasonGradesTeamPitchingTable
import com.advanced_baseball_stats.v2.repository.stats.tables.TeamsStatsPitchingPerSeasonTable

import org.ktorm.dsl.from
import org.ktorm.dsl.select
import org.ktorm.dsl.where
import org.ktorm.dsl.eq
import org.ktorm.dsl.and
import org.ktorm.dsl.forEachIndexed
import org.ktorm.dsl.map

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
}