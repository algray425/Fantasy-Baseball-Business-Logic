package com.advanced_baseball_stats.v2.repository.stats

import com.advanced_baseball_stats.v2.model.teams.hitting.TeamStatsHitting
import com.advanced_baseball_stats.v2.repository.stats.tables.SeasonGradesTeamHittingTable
import com.advanced_baseball_stats.v2.repository.stats.tables.TeamsStatsHittingPerSeasonTable

import org.ktorm.dsl.from
import org.ktorm.dsl.select
import org.ktorm.dsl.where
import org.ktorm.dsl.eq
import org.ktorm.dsl.and
import org.ktorm.dsl.forEachIndexed
import org.ktorm.dsl.map

object TeamHittingSql
{
    fun getHittingGradesByTeam(team: String, season: Int): Double?
    {
        DatabaseConnection.database.from(SeasonGradesTeamHittingTable)
            .select(SeasonGradesTeamHittingTable.percentileOverall)
            .where{(SeasonGradesTeamHittingTable.team eq team) and (SeasonGradesTeamHittingTable.season eq season)}
            .forEachIndexed{ index,teamRow ->
                if (index == 0)
                {
                    return teamRow[SeasonGradesTeamHittingTable.percentileOverall]
                }
            }
        return null
    }

    fun getTeamHittingStatsPerSeason(teamId: String, season: Int): TeamStatsHitting?
    {
        return DatabaseConnection.database.from(TeamsStatsHittingPerSeasonTable)
            .select()
            .where {
                (TeamsStatsHittingPerSeasonTable.team eq teamId) and (TeamsStatsHittingPerSeasonTable.season eq season)
            }
            .map { row ->
                TeamStatsHitting(
                    team = row[TeamsStatsHittingPerSeasonTable.team] ?: "",
                    season = row[TeamsStatsHittingPerSeasonTable.season] ?: 0,
                    plateAppearances = row[TeamsStatsHittingPerSeasonTable.plateAppearances] ?: 0,
                    atBats = row[TeamsStatsHittingPerSeasonTable.atBats] ?: 0,
                    runs = row[TeamsStatsHittingPerSeasonTable.runs] ?: 0,
                    hits = row[TeamsStatsHittingPerSeasonTable.hits] ?: 0,
                    singles = row[TeamsStatsHittingPerSeasonTable.singles] ?: 0,
                    doubles = row[TeamsStatsHittingPerSeasonTable.doubles] ?: 0,
                    triples = row[TeamsStatsHittingPerSeasonTable.triples] ?: 0,
                    homeRuns = row[TeamsStatsHittingPerSeasonTable.homeRuns] ?: 0,
                    rbis = row[TeamsStatsHittingPerSeasonTable.rbis] ?: 0,
                    sacHits = row[TeamsStatsHittingPerSeasonTable.sacHits] ?: 0,
                    sacFlies = row[TeamsStatsHittingPerSeasonTable.sacFlies] ?: 0,
                    hitByPitch = row[TeamsStatsHittingPerSeasonTable.hitByPitch] ?: 0,
                    walks = row[TeamsStatsHittingPerSeasonTable.walks] ?: 0,
                    intentionalWalks = row[TeamsStatsHittingPerSeasonTable.intentionalWalks] ?: 0,
                    strikeOuts = row[TeamsStatsHittingPerSeasonTable.strikeOuts] ?: 0,
                    stolenBases = row[TeamsStatsHittingPerSeasonTable.stolenBases] ?: 0,
                    caughtStealing = row[TeamsStatsHittingPerSeasonTable.caughtStealing] ?: 0,
                    groundIntoDoublePlays = row[TeamsStatsHittingPerSeasonTable.groundIntoDoublePlays] ?: 0,
                    catcherInterference = row[TeamsStatsHittingPerSeasonTable.catcherInterference] ?: 0,
                    battingAverage = row[TeamsStatsHittingPerSeasonTable.battingAverage] ?: 0.0,
                    onBasePercentage = row[TeamsStatsHittingPerSeasonTable.onBasePercentage] ?: 0.0,
                    sluggingPercentage = row[TeamsStatsHittingPerSeasonTable.sluggingPercentage] ?: 0.0,
                    onBasePlusSlugging = row[TeamsStatsHittingPerSeasonTable.onBasePlusSlugging] ?: 0.0,
                    eraAgainst = row[TeamsStatsHittingPerSeasonTable.eraAgainst] ?: 0.0,
                    whipAgainst = row[TeamsStatsHittingPerSeasonTable.whipAgainst] ?: 0.0,
                    ksPerNineAgainst = row[TeamsStatsHittingPerSeasonTable.ksPerNineAgainst] ?: 0.0,
                    qualityStartsAgainst = row[TeamsStatsHittingPerSeasonTable.qualityStartsAgainst] ?: 0,
                    savesAgainst = row[TeamsStatsHittingPerSeasonTable.savesAgainst] ?: 0,
                    holdsAgainst = row[TeamsStatsHittingPerSeasonTable.holdsAgainst] ?: 0
                )
            }.firstOrNull()
    }
}