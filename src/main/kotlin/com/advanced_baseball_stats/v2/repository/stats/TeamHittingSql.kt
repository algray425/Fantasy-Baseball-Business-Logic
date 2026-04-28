package com.advanced_baseball_stats.v2.repository.stats

import com.advanced_baseball_stats.v2.model.teams.hitting.TeamStatsHitting
import com.advanced_baseball_stats.v2.model.teams.overall.TeamSeasonStats
import com.advanced_baseball_stats.v2.repository.stats.tables.SeasonGradesTeamHittingTable
import com.advanced_baseball_stats.v2.repository.stats.tables.SeasonGradesTeamPitchingTable
import com.advanced_baseball_stats.v2.repository.stats.tables.TeamsStatsHittingPerSeasonTable
import com.advanced_baseball_stats.v2.repository.stats.tables.TeamsStatsPitchingPerSeasonTable
import org.ktorm.dsl.*
import org.ktorm.expression.OrderByExpression

object TeamHittingSql
{
    val sortByToOrderBy: Map<String, OrderByExpression> = mapOf(
        "RUNS"                      to TeamsStatsHittingPerSeasonTable.runs.desc(),
        "HOME_RUNS"                 to TeamsStatsHittingPerSeasonTable.homeRuns.desc(),
        "RBIS"                      to TeamsStatsHittingPerSeasonTable.rbis.desc(),
        "STOLEN_BASES"              to TeamsStatsHittingPerSeasonTable.stolenBases.desc(),
        "OBP"                       to TeamsStatsHittingPerSeasonTable.onBasePercentage.desc(),
        "QUALITY_STARTS_AGAINST"    to TeamsStatsHittingPerSeasonTable.qualityStartsAgainst.desc(),
        "SAVES_PLUS_HOLDS_AGAINST"  to sum(TeamsStatsHittingPerSeasonTable.savesAgainst plus TeamsStatsHittingPerSeasonTable.holdsAgainst).asc(),
        "ERA_AGAINST"               to TeamsStatsHittingPerSeasonTable.eraAgainst.desc(),
        "WHIP_AGAINST"              to TeamsStatsHittingPerSeasonTable.whipAgainst.desc(),
        "KS_PER_NINE_AGAINST"       to TeamsStatsHittingPerSeasonTable.ksPerNineAgainst.desc()
    )

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

    fun getAllTeamHittingStatsPerSeason(season: Int, sortBy: String): List<TeamStatsHitting>
    {
        val teamStatsHitting = mutableListOf<TeamStatsHitting>()

        var query = DatabaseConnection.database.from(TeamsStatsHittingPerSeasonTable)
            .select()
            .where { TeamsStatsHittingPerSeasonTable.season eq season }

        if (sortBy.isNotEmpty() && sortByToOrderBy.containsKey(sortBy))
        {
            query = query.orderBy(sortByToOrderBy[sortBy]!!)
        }

        query.forEach { row ->
            teamStatsHitting.add(TeamStatsHitting(
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
            ))
        }

        return teamStatsHitting
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

    fun getTeamSummary(team: String, season: Int): TeamSeasonStats?
    {
        return DatabaseConnection.database
            .from(TeamsStatsHittingPerSeasonTable)
            .innerJoin(TeamsStatsPitchingPerSeasonTable, TeamsStatsPitchingPerSeasonTable.team eq TeamsStatsHittingPerSeasonTable.team)
            .innerJoin(SeasonGradesTeamHittingTable, SeasonGradesTeamHittingTable.team eq TeamsStatsPitchingPerSeasonTable.team)
            .innerJoin(SeasonGradesTeamPitchingTable, SeasonGradesTeamPitchingTable.team eq TeamsStatsPitchingPerSeasonTable.team)
            .select(
                TeamsStatsPitchingPerSeasonTable.team,
                TeamsStatsPitchingPerSeasonTable.season,
                TeamsStatsPitchingPerSeasonTable.runs,
                TeamsStatsPitchingPerSeasonTable.homeRuns,
                TeamsStatsPitchingPerSeasonTable.rbisAgainst,
                TeamsStatsPitchingPerSeasonTable.stolenBases,
                TeamsStatsPitchingPerSeasonTable.onBasePercentageAgainst,
                TeamsStatsHittingPerSeasonTable.eraAgainst,
                TeamsStatsHittingPerSeasonTable.whipAgainst,
                TeamsStatsHittingPerSeasonTable.ksPerNineAgainst,
                TeamsStatsHittingPerSeasonTable.qualityStartsAgainst,
                TeamsStatsHittingPerSeasonTable.savesAgainst,
                TeamsStatsHittingPerSeasonTable.holdsAgainst,
                SeasonGradesTeamHittingTable.percentileEra,
                SeasonGradesTeamHittingTable.percentileWhip,
                SeasonGradesTeamHittingTable.percentileKsPerNine,
                SeasonGradesTeamHittingTable.percentileQualityStarts,
                SeasonGradesTeamHittingTable.percentileSavesAndHolds,
                SeasonGradesTeamHittingTable.percentileStartingPitchers,
                SeasonGradesTeamHittingTable.percentileReliefPitchers,
                SeasonGradesTeamHittingTable.percentileOverall,
                SeasonGradesTeamPitchingTable.percentileRuns,
                SeasonGradesTeamPitchingTable.percentileHomeRuns,
                SeasonGradesTeamPitchingTable.percentileRbis,
                SeasonGradesTeamPitchingTable.percentileStolenBases,
                SeasonGradesTeamPitchingTable.percentileOnBasePercentage,
                SeasonGradesTeamPitchingTable.percentileOverall
            )
            .where {
                (TeamsStatsHittingPerSeasonTable.team eq team) and
                        (TeamsStatsHittingPerSeasonTable.season eq season) and
                        (TeamsStatsPitchingPerSeasonTable.season eq season) and
                        (SeasonGradesTeamHittingTable.season eq season) and
                        (SeasonGradesTeamPitchingTable.season eq season)
            }
            .map { row ->
                TeamSeasonStats(
                    team = row[TeamsStatsPitchingPerSeasonTable.team]!!,
                    season = row[TeamsStatsPitchingPerSeasonTable.season]!!,
                    runs = row[TeamsStatsPitchingPerSeasonTable.runs]!!,
                    homeRuns = row[TeamsStatsPitchingPerSeasonTable.homeRuns]!!,
                    rbisAgainst = row[TeamsStatsPitchingPerSeasonTable.rbisAgainst]!!,
                    stolenBases = row[TeamsStatsPitchingPerSeasonTable.stolenBases]!!,
                    onBasePercentageAgainst = row[TeamsStatsPitchingPerSeasonTable.onBasePercentageAgainst]!!,
                    eraAgainst = row[TeamsStatsHittingPerSeasonTable.eraAgainst]!!,
                    whipAgainst = row[TeamsStatsHittingPerSeasonTable.whipAgainst]!!,
                    ksPerNineAgainst = row[TeamsStatsHittingPerSeasonTable.ksPerNineAgainst]!!,
                    qualityStartsAgainst = row[TeamsStatsHittingPerSeasonTable.qualityStartsAgainst]!!,
                    savesAgainst = row[TeamsStatsHittingPerSeasonTable.savesAgainst]!!,
                    holdsAgainst = row[TeamsStatsHittingPerSeasonTable.holdsAgainst]!!,
                    percentileEra = row[SeasonGradesTeamHittingTable.percentileEra]!!,
                    percentileWhip = row[SeasonGradesTeamHittingTable.percentileWhip]!!,
                    percentileKsPerNine = row[SeasonGradesTeamHittingTable.percentileKsPerNine]!!,
                    percentileQualityStarts = row[SeasonGradesTeamHittingTable.percentileQualityStarts]!!,
                    percentileSavesAndHolds = row[SeasonGradesTeamHittingTable.percentileSavesAndHolds]!!,
                    percentileStartingPitchers = row[SeasonGradesTeamHittingTable.percentileStartingPitchers]!!,
                    percentileReliefPitchers = row[SeasonGradesTeamHittingTable.percentileReliefPitchers]!!,
                    percentileHittingOverall = row[SeasonGradesTeamHittingTable.percentileOverall]!!,
                    percentileRuns = row[SeasonGradesTeamPitchingTable.percentileRuns]!!,
                    percentileHomeRuns = row[SeasonGradesTeamPitchingTable.percentileHomeRuns]!!,
                    percentileRbis = row[SeasonGradesTeamPitchingTable.percentileRbis]!!,
                    percentileStolenBases = row[SeasonGradesTeamPitchingTable.percentileStolenBases]!!,
                    percentileOnBasePercentage = row[SeasonGradesTeamPitchingTable.percentileOnBasePercentage]!!,
                    percentilePitchingOverall = row[SeasonGradesTeamPitchingTable.percentileOverall]!!
                )
            }
            .firstOrNull()
    }
}