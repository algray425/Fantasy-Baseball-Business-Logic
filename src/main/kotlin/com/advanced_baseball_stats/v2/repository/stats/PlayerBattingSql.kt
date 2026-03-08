package com.advanced_baseball_stats.v2.repository.stats

import com.advanced_baseball_stats.v2.helper.AgeHelper
import com.advanced_baseball_stats.v2.model.batters.*
import com.advanced_baseball_stats.v2.model.batters.Fantasy.FantasyPlayerSummary
import com.advanced_baseball_stats.v2.model.batters.Fantasy.FantasyPlayerSummaryBatting
import com.advanced_baseball_stats.v2.model.batters.Fantasy.FantasyPlayerSummaryReliefPitching
import com.advanced_baseball_stats.v2.model.batters.Fantasy.FantasyPlayerSummaryStartingPitching
import com.advanced_baseball_stats.v2.model.common.GameStat
import com.advanced_baseball_stats.v2.repository.stats.tables.*

import org.ktorm.dsl.*
import org.ktorm.expression.ColumnDeclaringExpression
import org.ktorm.schema.Column
import org.ktorm.schema.SqlType

import java.math.RoundingMode

object PlayerBattingSql
{
    private val dateRangeObpColumn = ((sum(PerGameStatsHittingTable.hits.cast(SqlType.of<Double>()!!)) plus sum(PerGameStatsHittingTable.hitByPitch.cast(SqlType.of<Double>()!!)) plus sum(PerGameStatsHittingTable.walks.cast(SqlType.of<Double>()!!))) div (sum(PerGameStatsHittingTable.atBats.cast(SqlType.of<Double>()!!)) plus sum(PerGameStatsHittingTable.walks.cast(SqlType.of<Double>()!!)) plus sum(PerGameStatsHittingTable.hitByPitch.cast(SqlType.of<Double>()!!)) plus sum(PerGameStatsHittingTable.sacFlies.cast(SqlType.of<Double>()!!)))).aliased("obp")

    private val sortByToRankingColumn: Map<String, Column<*>> = mapOf(
        "PERCENTILE_OVERALL"    to SeasonGradesTable.percentileOverall,
        "RUNS"                  to SeasonStatsHittingTable.runs,
        "HOME_RUNS"             to SeasonStatsHittingTable.homeRuns,
        "RBIS"                  to SeasonStatsHittingTable.rbis,
        "STOLEN_BASES"          to SeasonStatsHittingTable.stolenBases,
        "OBP"                   to SeasonStatsHittingTable.onBasePercentage
    )

    private val sortByToRankingColumnInDateRange: Map<String, ColumnDeclaringExpression<*>> = mapOf(
        "PERCENTILE_OVERALL"    to SeasonGradesTable.percentileOverall.aliased("percentileOverall"),
        "RUNS"                  to sum(SeasonStatsHittingTable.runs).aliased("runs"),
        "HOME_RUNS"             to sum(SeasonStatsHittingTable.homeRuns).aliased("homeRuns"),
        "RBIS"                  to sum(SeasonStatsHittingTable.rbis).aliased("rbis"),
        "STOLEN_BASES"          to sum(SeasonStatsHittingTable.stolenBases).aliased("stolenBases"),
        "OBP"                   to dateRangeObpColumn
    )

    private val sortByToProjectionColumn: Map<String, Column<*>> = mapOf(
        "PERCENTILE_OVERALL"    to HitterProjectionsTable.overallGradePercentile,
        "RUNS"                  to HitterProjectionsTable.runs,
        "HOME_RUNS"             to HitterProjectionsTable.homeRuns,
        "RBIS"                  to HitterProjectionsTable.rbis,
        "STOLEN_BASES"          to HitterProjectionsTable.stolenBases,
        "OBP"                   to HitterProjectionsTable.onBasePercentage
    )

    private val statToPerGameColumn: Map<String, Column<*>> = mapOf(
        "RUNS" to PerGameStatsHittingTable.runs,
        "HOME_RUNS" to PerGameStatsHittingTable.homeRuns,
        "RBIS" to PerGameStatsHittingTable.rbis,
        "STOLEN_BASES" to PerGameStatsHittingTable.stolenBases,
        "OBP" to PerGameStatsHittingTable.onBasePercentage
    )

    fun getBattersRankedByStat(season: Int, sortBy: String, positions: List<String>, espnIdFilter: MutableList<Int>, limit: Int, page: Int): MutableList<SeasonRankedBatter>
    {
        val rankedBattersList = mutableListOf<SeasonRankedBatter>()
        val offset = page * limit

        val columnToSortBy = sortByToRankingColumn[sortBy] ?: SeasonGradesTable.percentileOverall

        DatabaseConnection.database.from(SeasonStatsHittingTable)
            .innerJoin(BiosTable, on = SeasonStatsHittingTable.playerId eq BiosTable.playerId)
            .innerJoin(SeasonGradesTable, on = SeasonStatsHittingTable.playerId eq SeasonGradesTable.playerId)
            .select(BiosTable.playerId, BiosTable.firstName, BiosTable.lastName, BiosTable.currentTeam, BiosTable.currentPosition, SeasonGradesTable.percentileOverall,
                SeasonStatsHittingTable.runs, SeasonStatsHittingTable.homeRuns, SeasonStatsHittingTable.rbis, SeasonStatsHittingTable.stolenBases,
                SeasonStatsHittingTable.onBasePercentage)
            .whereWithConditions {
                it += SeasonStatsHittingTable   .season eq season
                it += SeasonGradesTable         .season eq season

                if (positions.isNotEmpty())
                {
                    it += BiosTable.currentPosition inList positions
                }
                if (espnIdFilter.isNotEmpty())
                {
                    it += BiosTable.espnId notInList espnIdFilter
                }
            }
            .orderBy(columnToSortBy.desc())
            .limit(limit)
            .offset(offset)
            .forEach { batterRow ->
                val playerId        = batterRow[BiosTable.playerId                      ] ?: ""
                val firstName       = batterRow[BiosTable.firstName                     ] ?: ""
                val lastName        = batterRow[BiosTable.lastName                      ] ?: ""
                val team            = batterRow[BiosTable.currentTeam                   ] ?: ""
                val position        = batterRow[BiosTable.currentPosition               ] ?: ""
                val grade           = batterRow[SeasonGradesTable.percentileOverall     ] ?: 0.0
                val runs            = batterRow[SeasonStatsHittingTable.runs            ] ?: 0
                val homeRuns        = batterRow[SeasonStatsHittingTable.homeRuns        ] ?: 0
                val rbis            = batterRow[SeasonStatsHittingTable.rbis            ] ?: 0
                val stolenBases     = batterRow[SeasonStatsHittingTable.stolenBases     ] ?: 0
                val obp             = batterRow[SeasonStatsHittingTable.onBasePercentage] ?: 0.0

                val roundedObp = obp.toBigDecimal().setScale(3, RoundingMode.HALF_UP).toDouble()

                rankedBattersList.add(SeasonRankedBatter(playerId, firstName, lastName, team, position, grade, runs, homeRuns, rbis, stolenBases, roundedObp))
            }

        return rankedBattersList
    }

    fun getRankedBattersByStatInDateRange(season: Int, sortBy: String, positions: List<String>, startDate: String, endDate: String, espnIdFilter: MutableList<Int>, limit: Int, page: Int): MutableList<SeasonRankedBatter>
    {
        val rankedBattersList = mutableListOf<SeasonRankedBatter>()

        val offset = page * limit

        val dateRanges: ClosedRange<String> = startDate..endDate

        var query = DatabaseConnection.database.from(PerGameStatsHittingTable)
            .innerJoin(GamesTable, on = GamesTable.gameId eq PerGameStatsHittingTable.gameId)
            .innerJoin(BiosTable, on = BiosTable.playerId eq PerGameStatsHittingTable.playerId)
            .innerJoin(SeasonGradesTable, on = SeasonGradesTable.playerId eq PerGameStatsHittingTable.playerId)
            .select(PerGameStatsHittingTable.playerId, BiosTable.firstName, BiosTable.lastName, BiosTable.currentTeam, BiosTable.currentPosition,
                SeasonGradesTable.percentileOverall.aliased("percentileOverall"), sum(PerGameStatsHittingTable.runs).aliased("runs"),
                sum(PerGameStatsHittingTable.homeRuns).aliased("homeRuns"), sum(PerGameStatsHittingTable.rbis).aliased("rbis"),
                sum(PerGameStatsHittingTable.stolenBases).aliased("stolenBases"), dateRangeObpColumn)
            .whereWithConditions {
                it += GamesTable.date between dateRanges
                it += SeasonGradesTable.season eq season

                if (positions.isNotEmpty())
                {
                    it += BiosTable.currentPosition inList positions
                }
                if (espnIdFilter.isNotEmpty())
                {
                    it += BiosTable.espnId notInList espnIdFilter
                }
            }
            .limit(limit)
            .offset(offset)
            .groupBy(PerGameStatsHittingTable.playerId)

            if (sortByToRankingColumnInDateRange.containsKey(sortBy))
            {
                query = query.orderBy(sortByToRankingColumnInDateRange[sortBy]!!.desc())
            }

            query.forEach { playerRow ->
                val playerId            = playerRow[PerGameStatsHittingTable.playerId] ?: ""
                val firstName           = playerRow[BiosTable.firstName] ?: ""
                val lastName            = playerRow[BiosTable.lastName] ?: ""
                val currentTeam         = playerRow[BiosTable.currentTeam] ?: ""
                val currentPosition     = playerRow[BiosTable.currentPosition] ?: ""
                val percentileOverall   = playerRow[SeasonGradesTable.percentileOverall.aliased("percentileOverall")] ?: 0.0
                val runs                = playerRow[sum(PerGameStatsHittingTable.runs).aliased("runs")] ?: 0
                val homeRuns            = playerRow[sum(PerGameStatsHittingTable.homeRuns).aliased("homeRuns")] ?: 0
                val rbis                = playerRow[sum(PerGameStatsHittingTable.rbis).aliased("rbis")] ?: 0
                val stolenBases         = playerRow[sum(PerGameStatsHittingTable.stolenBases).aliased("stolenBases")] ?: 0
                val obp                 = playerRow[dateRangeObpColumn] ?: 0.0

                val roundedObp = obp.toBigDecimal().setScale(3, RoundingMode.HALF_UP).toDouble()

                rankedBattersList.add(SeasonRankedBatter(playerId, firstName, lastName, currentTeam, currentPosition, percentileOverall, runs, homeRuns,
                    rbis, stolenBases, roundedObp))
            }

        return rankedBattersList
    }

    fun getBatterProjections(sortBy: String, qualified: Boolean, positions: List<String>, espnLeagueIds: List<Int>, limit: Int, page: Int): MutableList<BatterProjection> {
        val projections = mutableListOf<BatterProjection>()
        val offset = page * limit

        val columnToSortBy = sortByToProjectionColumn[sortBy] ?: HitterProjectionsTable.overallGradePercentile

        DatabaseConnection.database.from(HitterProjectionsTable)
            .innerJoin(BiosTable, on = HitterProjectionsTable.playerId eq BiosTable.playerId)
            .select(
                BiosTable.playerId, BiosTable.firstName, BiosTable.lastName, BiosTable.currentTeam, BiosTable.currentPosition, HitterProjectionsTable.runs, HitterProjectionsTable.homeRuns, HitterProjectionsTable.rbis,
                HitterProjectionsTable.stolenBases, HitterProjectionsTable.onBasePercentage, HitterProjectionsTable.overallPercentileRuns, HitterProjectionsTable.overallPercentileHomeRuns,
                HitterProjectionsTable.overallPercentileRbis, HitterProjectionsTable.overallPercentileStolenBases, HitterProjectionsTable.overallPercentileObp, HitterProjectionsTable.overallGradePercentile,
                HitterProjectionsTable.qualifiedPercentileRuns, HitterProjectionsTable.qualifiedPercentileHomeRuns, HitterProjectionsTable.qualifiedPercentileRbis, HitterProjectionsTable.qualifiedPercentileStolenBases,
                HitterProjectionsTable.qualifiedPercentileObp, HitterProjectionsTable.qualifiedGradePercentile)
            .whereWithConditions {
                if (positions.isNotEmpty())
                {
                    it += BiosTable.currentPosition inList positions
                }
                if (espnLeagueIds.isNotEmpty())
                {
                    it += BiosTable.espnId notInList espnLeagueIds
                }
                if (qualified)
                {
                    it += HitterProjectionsTable.qualified eq 1
                }
            }
            .orderBy(columnToSortBy.desc())
            .limit(limit)
            .offset(offset)
            .forEach { projectionRow ->
                val playerId    = projectionRow[BiosTable.playerId] ?: ""
                val firstName   = projectionRow[BiosTable.firstName] ?: ""
                val lastName    = projectionRow[BiosTable.lastName] ?: ""
                val team        = projectionRow[BiosTable.currentTeam] ?: ""
                val position    = projectionRow[BiosTable.currentPosition] ?: ""

                val runs        = projectionRow[HitterProjectionsTable.runs] ?: -1
                val homeRuns    = projectionRow[HitterProjectionsTable.homeRuns] ?: -1
                val rbis        = projectionRow[HitterProjectionsTable.rbis] ?: -1
                val stolenBases = projectionRow[HitterProjectionsTable.stolenBases] ?: -1
                val obp         = projectionRow[HitterProjectionsTable.onBasePercentage] ?: -1.0

                val overallPercentileRuns           = projectionRow[HitterProjectionsTable.overallPercentileRuns] ?: -1.0
                val overallPercentileHomeRuns       = projectionRow[HitterProjectionsTable.overallPercentileHomeRuns] ?: -1.0
                val overallPercentileRbis           = projectionRow[HitterProjectionsTable.overallPercentileRbis] ?: -1.0
                val overallPercentileStolenBases    = projectionRow[HitterProjectionsTable.overallPercentileStolenBases] ?: -1.0
                val overallPercentileObp            = projectionRow[HitterProjectionsTable.overallPercentileObp] ?: -1.0
                val overallPercentileGrade          = projectionRow[HitterProjectionsTable.overallGradePercentile] ?: -1.0

                val qualifiedPercentileRuns           = projectionRow[HitterProjectionsTable.qualifiedPercentileRuns] ?: -1.0
                val qualifiedPercentileHomeRuns       = projectionRow[HitterProjectionsTable.qualifiedPercentileHomeRuns] ?: -1.0
                val qualifiedPercentileRbis           = projectionRow[HitterProjectionsTable.qualifiedPercentileRbis] ?: -1.0
                val qualifiedPercentileStolenBases    = projectionRow[HitterProjectionsTable.qualifiedPercentileStolenBases] ?: -1.0
                val qualifiedPercentileObp            = projectionRow[HitterProjectionsTable.qualifiedPercentileObp] ?: -1.0
                val qualifiedPercentileGrade          = projectionRow[HitterProjectionsTable.qualifiedGradePercentile] ?: -1.0

                val playerProjection = BatterProjection(playerId, firstName, lastName, team, position, runs, homeRuns, rbis, stolenBases, obp,
                    overallPercentileRuns, overallPercentileHomeRuns, overallPercentileRbis, overallPercentileStolenBases, overallPercentileObp, overallPercentileGrade,
                    qualifiedPercentileRuns, qualifiedPercentileHomeRuns, qualifiedPercentileRbis, qualifiedPercentileStolenBases, qualifiedPercentileObp, qualifiedPercentileGrade)

                projections.add(playerProjection)
            }
            return projections
        }

    fun getPlayerSummary(playerId: String): BatterSummary?
    {
        var batterSummary: BatterSummary? = null

        DatabaseConnection.database.from(BiosTable)
            .innerJoin(HitterProjectionsTable, on = BiosTable.playerId eq HitterProjectionsTable.playerId)
            .select(
                BiosTable.playerId, BiosTable.firstName, BiosTable.lastName, BiosTable.dob, BiosTable.batSide, BiosTable.throwHand,
                BiosTable.height, BiosTable.weight,
                BiosTable.currentTeam, BiosTable.currentPosition, HitterProjectionsTable.runs, HitterProjectionsTable.homeRuns, HitterProjectionsTable.rbis, HitterProjectionsTable.stolenBases,
                HitterProjectionsTable.onBasePercentage, HitterProjectionsTable.overallPercentileRuns, HitterProjectionsTable.overallPercentileHomeRuns, HitterProjectionsTable.overallPercentileRbis,
                HitterProjectionsTable.overallPercentileStolenBases, HitterProjectionsTable.overallPercentileObp, HitterProjectionsTable.overallGradePercentile, HitterProjectionsTable.qualifiedPercentileRuns,
                HitterProjectionsTable.qualifiedPercentileHomeRuns, HitterProjectionsTable.qualifiedPercentileRbis, HitterProjectionsTable.qualifiedPercentileStolenBases,
                HitterProjectionsTable.qualifiedPercentileObp, HitterProjectionsTable.qualifiedGradePercentile)
            .where { BiosTable.playerId eq playerId }
            .limit(1)
            .forEach { playerRow ->
                val playerId                        = playerRow[BiosTable.playerId] ?: ""
                val firstName                       = playerRow[BiosTable.firstName] ?: ""
                val lastName                        = playerRow[BiosTable.lastName] ?: ""
                val dob                             = playerRow[BiosTable.dob] ?: 0
                val batSide                         = playerRow[BiosTable.batSide] ?: ""
                val throwHand                       = playerRow[BiosTable.throwHand] ?: ""
                val height                          = playerRow[BiosTable.height] ?: ""
                val weight                          = playerRow[BiosTable.weight] ?: 0.0
                val currentTeam                     = playerRow[BiosTable.currentTeam] ?: ""
                val currentPosition                 = playerRow[BiosTable.currentPosition] ?: ""
                val runs                            = playerRow[HitterProjectionsTable.runs] ?: 0
                val homeRuns                        = playerRow[HitterProjectionsTable.homeRuns] ?: 0
                val rbis                            = playerRow[HitterProjectionsTable.rbis] ?: 0
                val stolenBases                     = playerRow[HitterProjectionsTable.stolenBases] ?: 0
                val onBasePercentage                = playerRow[HitterProjectionsTable.onBasePercentage] ?: 0.0
                val overallPercentileRuns           = playerRow[HitterProjectionsTable.overallPercentileRuns] ?: 0.0
                val overallPercentileHomeRuns       = playerRow[HitterProjectionsTable.overallPercentileHomeRuns] ?: 0.0
                val overallPercentileRbis           = playerRow[HitterProjectionsTable.overallPercentileRbis] ?: 0.0
                val overallPercentileStolenBases    = playerRow[HitterProjectionsTable.overallPercentileStolenBases] ?: 0.0
                val overallPercentileObp            = playerRow[HitterProjectionsTable.overallPercentileObp] ?: 0.0
                val overallGradePercentile          = playerRow[HitterProjectionsTable.overallGradePercentile] ?: 0.0
                val qualifiedPercentileRuns         = playerRow[HitterProjectionsTable.qualifiedPercentileRuns] ?: 0.0
                val qualifiedPercentileHomeRuns     = playerRow[HitterProjectionsTable.qualifiedPercentileHomeRuns] ?: 0.0
                val qualifiedPercentileRbis         = playerRow[HitterProjectionsTable.qualifiedPercentileRbis] ?: 0.0
                val qualifiedPercentileStolenBases  = playerRow[HitterProjectionsTable.qualifiedPercentileStolenBases] ?: 0.0
                val qualifiedPercentileObp          = playerRow[HitterProjectionsTable.qualifiedPercentileObp] ?: 0.0
                val qualifiedGradePercentile        = playerRow[HitterProjectionsTable.qualifiedGradePercentile] ?: 0.0

                batterSummary = BatterSummary(playerId, firstName, lastName, AgeHelper.calculateAgeFromTimestamp(dob), batSide, throwHand, height, weight, currentTeam, currentPosition, runs, homeRuns,
                    rbis, stolenBases, onBasePercentage, overallPercentileRuns, overallPercentileHomeRuns, overallPercentileRbis, overallPercentileStolenBases,
                    overallPercentileObp, overallGradePercentile, qualifiedPercentileRuns, qualifiedPercentileHomeRuns, qualifiedPercentileRbis, qualifiedPercentileStolenBases,
                    qualifiedPercentileObp, qualifiedGradePercentile)
            }

        return batterSummary
    }

    fun getSeasonSummariesForHitter(playerId: String, startSeason: String): MutableList<BatterSeasonSummary> {
        val seasonSummaries = mutableListOf<BatterSeasonSummary>()

        DatabaseConnection.database.from(SeasonStatsHittingTable)
            .select(
                SeasonStatsHittingTable.season,
                SeasonStatsHittingTable.teams,
                SeasonStatsHittingTable.plateAppearances,
                SeasonStatsHittingTable.atBats,
                SeasonStatsHittingTable.runs,
                SeasonStatsHittingTable.hits,
                SeasonStatsHittingTable.doubles,
                SeasonStatsHittingTable.triples,
                SeasonStatsHittingTable.homeRuns,
                SeasonStatsHittingTable.rbis,
                SeasonStatsHittingTable.walks,
                SeasonStatsHittingTable.strikeOuts,
                SeasonStatsHittingTable.stolenBases,
                SeasonStatsHittingTable.battingAverage,
                SeasonStatsHittingTable.onBasePercentage,
                SeasonStatsHittingTable.sluggingPercentage,
                SeasonStatsHittingTable.onBasePlusSlugging,
                SeasonStatsHittingTable.babip,
                SeasonStatsHittingTable.spd,
                SeasonStatsHittingTable.groundBallPercentage,
                SeasonStatsHittingTable.flyBallPercentage,
                SeasonStatsHittingTable.lineDrivePercentage,
                SeasonStatsHittingTable.popUpPercentage,
                SeasonStatsHittingTable.hardHitPercentage,
                SeasonStatsHittingTable.barrelPercentage)
            .where{ (SeasonStatsHittingTable.season greaterEq startSeason.toInt()) and (SeasonStatsHittingTable.playerId eq playerId) }
            .forEach { seasonRow ->
                val season                  = seasonRow[SeasonStatsHittingTable.season] ?: -1
                val teams                   = seasonRow[SeasonStatsHittingTable.teams] ?: ""
                val plateAppearances        = seasonRow[SeasonStatsHittingTable.plateAppearances] ?: -1
                val atBats                  = seasonRow[SeasonStatsHittingTable.atBats]?: -1
                val runs                    = seasonRow[SeasonStatsHittingTable.runs]?: -1
                val hits                    = seasonRow[SeasonStatsHittingTable.hits]?: -1
                val doubles                 = seasonRow[SeasonStatsHittingTable.doubles]?: -1
                val triples                 = seasonRow[SeasonStatsHittingTable.triples]?: -1
                val homeRuns                = seasonRow[SeasonStatsHittingTable.homeRuns]?: -1
                val rbis                    = seasonRow[SeasonStatsHittingTable.rbis]?: -1
                val walks                   = seasonRow[SeasonStatsHittingTable.walks]?: -1
                val strikeOuts              = seasonRow[SeasonStatsHittingTable.strikeOuts]?: -1
                val stolenBases             = seasonRow[SeasonStatsHittingTable.stolenBases]?: -1
                val battingAverage          = seasonRow[SeasonStatsHittingTable.battingAverage]?: -1.0
                val onBasePercentage        = seasonRow[SeasonStatsHittingTable.onBasePercentage]?: -1.0
                val sluggingPercentage      = seasonRow[SeasonStatsHittingTable.sluggingPercentage]?: -1.0
                val onBasePlusSlugging      = seasonRow[SeasonStatsHittingTable.onBasePlusSlugging]?: -1.0
                val babip                   = seasonRow[SeasonStatsHittingTable.babip]?: -1.0
                val spd                     = seasonRow[SeasonStatsHittingTable.spd]?: -1.0
                val groundBallPercentage    = seasonRow[SeasonStatsHittingTable.groundBallPercentage]?: -1.0
                val flyBallPercentage       = seasonRow[SeasonStatsHittingTable.flyBallPercentage]?: -1.0
                val lineDrivePercentage     = seasonRow[SeasonStatsHittingTable.lineDrivePercentage]?: -1.0
                val popUpPercentage         = seasonRow[SeasonStatsHittingTable.popUpPercentage]?: -1.0
                val hardHitPercentage       = seasonRow[SeasonStatsHittingTable.hardHitPercentage]?: -1.0
                val barrelPercentage        = seasonRow[SeasonStatsHittingTable.barrelPercentage]?: -1.0

                seasonSummaries.add(BatterSeasonSummary(season, teams, plateAppearances, atBats, runs, hits, doubles, triples, homeRuns, rbis, walks,
                    strikeOuts, stolenBases, battingAverage, onBasePercentage, sluggingPercentage, onBasePlusSlugging, babip,
                    spd, groundBallPercentage, flyBallPercentage, lineDrivePercentage, popUpPercentage, hardHitPercentage, barrelPercentage))
            }

        return seasonSummaries
    }

    fun getHittingStatPerGame(playerId: String, season: Int, stat: String): MutableList<GameStat>
    {
        val batterGames = mutableListOf<GameStat>()

        val statColumn = statToPerGameColumn[stat] ?: PerGameStatsHittingTable.runs

        DatabaseConnection.database.from(PerGameStatsHittingTable)
            .innerJoin(GamesTable, on = PerGameStatsHittingTable.gameId eq GamesTable.gameId)
            .select(GamesTable.gameId, GamesTable.date, GamesTable.homeTeam, GamesTable.visTeam, statColumn)
            .where { (GamesTable.season eq season) and (PerGameStatsHittingTable.playerId eq playerId) }
            .forEach { gameRow ->
                val gameId      = gameRow[GamesTable.gameId] ?: ""
                val date        = gameRow[GamesTable.date] ?: ""
                val homeTeam    = gameRow[GamesTable.homeTeam] ?: ""
                val visTeam     = gameRow[GamesTable.visTeam] ?: ""
                val gameStat    = convertAnyToDouble(gameRow[statColumn]) ?: 0.0

                batterGames.add(GameStat(gameId, date, homeTeam, visTeam, gameStat))
            }
        return batterGames
    }

    fun getEspnFantasyPlayerSummaries(espnIds: List<Int>, season: Int): MutableList<FantasyPlayerSummary>
    {
        val playerSummaries: MutableList<FantasyPlayerSummary> = mutableListOf()

        DatabaseConnection.database.from(BiosTable)
            .innerJoin(SeasonStatsHittingTable, on = BiosTable.playerId eq SeasonStatsHittingTable.playerId)
            .innerJoin(SeasonGradesTable, on = BiosTable.playerId eq SeasonGradesTable.playerId)
            .select(BiosTable.playerId, BiosTable.firstName, BiosTable.lastName, BiosTable.currentPosition, BiosTable.currentTeam,
                SeasonGradesTable.percentileOverall, SeasonGradesTable.percentileOverallQualified, SeasonGradesTable.percentileRuns,
                SeasonGradesTable.percentileHomeRuns, SeasonGradesTable.percentileRbis, SeasonGradesTable.percentileStolenBases, SeasonGradesTable.percentileOnBasePercentage,
                SeasonStatsHittingTable.runs, SeasonStatsHittingTable.homeRuns, SeasonStatsHittingTable.rbis, SeasonStatsHittingTable.stolenBases,
                SeasonStatsHittingTable.onBasePercentage)
            .where{ (BiosTable.currentPosition neq "P") and (BiosTable.espnId inList espnIds) and (SeasonStatsHittingTable.season eq season) and (SeasonGradesTable.season eq season) }
            .orderBy(SeasonGradesTable.percentileOverall.desc())
            .forEach { playerRow ->
                val playerId                            = playerRow[BiosTable.playerId] ?: ""
                val firstName                           = playerRow[BiosTable.firstName] ?: ""
                val lastName                            = playerRow[BiosTable.lastName] ?: ""
                val currentPosition                     = playerRow[BiosTable.currentPosition] ?: ""
                val currentTeam                         = playerRow[BiosTable.currentTeam] ?: ""
                val percentileOverall                   = playerRow[SeasonGradesTable.percentileOverall] ?: 0.0
                val percentileOverallQualified          = playerRow[SeasonGradesTable.percentileOverallQualified] ?: 0.0
                val percentileOverallRuns               = playerRow[SeasonGradesTable.percentileRuns] ?: 0.0
                val percentileOverallHomeRuns           = playerRow[SeasonGradesTable.percentileHomeRuns] ?: 0.0
                val percentileOverallRbis               = playerRow[SeasonGradesTable.percentileRbis] ?: 0.0
                val percentileOverallStolenBases        = playerRow[SeasonGradesTable.percentileStolenBases] ?: 0.0
                val percentileOverallObp                = playerRow[SeasonGradesTable.percentileOnBasePercentage] ?: 0.0
                val runs                                = playerRow[SeasonStatsHittingTable.runs] ?: 0
                val homeRuns                            = playerRow[SeasonStatsHittingTable.homeRuns] ?: 0
                val rbis                                = playerRow[SeasonStatsHittingTable.rbis] ?: 0
                val stolenBases                         = playerRow[SeasonStatsHittingTable.stolenBases] ?: 0
                val onBasePercentage                    = playerRow[SeasonStatsHittingTable.onBasePercentage] ?: 0.0

                playerSummaries.add(
                    FantasyPlayerSummaryBatting(playerId, firstName, lastName, currentPosition, currentTeam, percentileOverall, percentileOverallQualified,
                        percentileOverallRuns, percentileOverallHomeRuns, percentileOverallRbis, percentileOverallStolenBases, percentileOverallObp,
                        runs, homeRuns, rbis, stolenBases, onBasePercentage)
                )
            }

        DatabaseConnection.database.from(BiosTable)
            .innerJoin(SeasonGradesStartingPitchersTable, on  = BiosTable.playerId eq SeasonGradesStartingPitchersTable.playerId)
            .innerJoin(SeasonStatsPitchingTable, on = SeasonGradesStartingPitchersTable.playerId eq SeasonStatsPitchingTable.playerId)
            .select(BiosTable.playerId, BiosTable.firstName, BiosTable.lastName, BiosTable.currentPosition, BiosTable.currentTeam,
                SeasonGradesStartingPitchersTable.percentileOverall, SeasonGradesStartingPitchersTable.percentileOverallQualified,
                SeasonGradesStartingPitchersTable.percentileQualityStarts, SeasonGradesStartingPitchersTable.percentileEra,
                SeasonGradesStartingPitchersTable.percentileWhip, SeasonGradesStartingPitchersTable.percentileKsPerNine,
                SeasonStatsPitchingTable.qualityStarts, SeasonStatsPitchingTable.era, SeasonStatsPitchingTable.whip,
                SeasonStatsPitchingTable.ksPerNine)
            .where{ (BiosTable.currentPosition eq "P") and (BiosTable.espnId inList espnIds) and (SeasonStatsPitchingTable.season eq season) and (SeasonGradesStartingPitchersTable.season eq season) }
            .orderBy(SeasonGradesStartingPitchersTable.percentileOverall.desc())
            .forEach { playerRow ->
                val playerId                = playerRow[BiosTable.playerId] ?: ""
                val firstName               = playerRow[BiosTable.firstName] ?: ""
                val lastName                = playerRow[BiosTable.lastName] ?: ""
                val currentPosition         = playerRow[BiosTable.currentPosition] ?: ""
                val currentTeam             = playerRow[BiosTable.currentTeam] ?: ""
                val overallPercentile       = playerRow[SeasonGradesStartingPitchersTable.percentileOverall] ?: 0.0
                val qualifiedPercentile     = playerRow[SeasonGradesStartingPitchersTable.percentileOverallQualified] ?: 0.0
                val percentileQualityStarts = playerRow[SeasonGradesStartingPitchersTable.percentileQualityStarts] ?: 0.0
                val percentileEra           = playerRow[SeasonGradesStartingPitchersTable.percentileEra] ?: 0.0
                val percentileWhip          = playerRow[SeasonGradesStartingPitchersTable.percentileWhip] ?: 0.0
                val percentileKsPerNine     = playerRow[SeasonGradesStartingPitchersTable.percentileKsPerNine] ?: 0.0
                val qualityStarts           = playerRow[SeasonStatsPitchingTable.qualityStarts] ?: 0
                val era                     = playerRow[SeasonStatsPitchingTable.era] ?: 0.0
                val whip                    = playerRow[SeasonStatsPitchingTable.whip] ?: 0.0
                val ksPerNine               = playerRow[SeasonStatsPitchingTable.ksPerNine] ?: 0.0


                playerSummaries.add(FantasyPlayerSummaryStartingPitching(playerId, firstName, lastName, currentPosition, currentTeam, overallPercentile,
                    qualifiedPercentile, percentileQualityStarts, percentileEra, percentileWhip, percentileKsPerNine, qualityStarts, era, whip, ksPerNine))
            }

        DatabaseConnection.database.from(BiosTable)
            .innerJoin(SeasonGradesReliefPitchersTable, on  = BiosTable.playerId eq SeasonGradesReliefPitchersTable.playerId)
            .innerJoin(SeasonStatsPitchingTable, on  = SeasonGradesReliefPitchersTable.playerId eq SeasonStatsPitchingTable.playerId)
            .select(BiosTable.playerId, BiosTable.firstName, BiosTable.lastName, BiosTable.currentPosition, BiosTable.currentTeam,
                SeasonGradesReliefPitchersTable.percentileOverall, SeasonGradesReliefPitchersTable.overallGradeQualified,
                SeasonGradesReliefPitchersTable.percentileSavesAndHolds, SeasonGradesReliefPitchersTable.percentileEra, SeasonGradesReliefPitchersTable.percentileWhip,
                SeasonGradesReliefPitchersTable.percentileKsPerNine, SeasonStatsPitchingTable.era, SeasonStatsPitchingTable.whip,
                SeasonStatsPitchingTable.ksPerNine, SeasonStatsPitchingTable.saves,
                SeasonStatsPitchingTable.holds)
            .where { (BiosTable.currentPosition eq "P") and (BiosTable.espnId inList espnIds) and (SeasonStatsPitchingTable.season eq season) and (SeasonGradesReliefPitchersTable.season eq season) }
            .orderBy(SeasonGradesReliefPitchersTable.percentileOverall.desc())
            .forEach { playerRow ->
                val playerId                        = playerRow[BiosTable.playerId] ?: ""
                val firstName                       = playerRow[BiosTable.firstName] ?: ""
                val lastName                        = playerRow[BiosTable.lastName] ?: ""
                val currentPosition                 = playerRow[BiosTable.currentPosition] ?: ""
                val currentTeam                     = playerRow[BiosTable.currentTeam] ?: ""
                val overallPercentile               = playerRow[SeasonGradesReliefPitchersTable.percentileOverall] ?: 0.0
                val qualifiedPercentile             = playerRow[SeasonGradesReliefPitchersTable.overallGradeQualified] ?: 0.0
                val overallPercentileSavesAndHolds  = playerRow[SeasonGradesReliefPitchersTable.percentileSavesAndHolds] ?: 0.0
                val overallPercentileEra            = playerRow[SeasonGradesReliefPitchersTable.percentileEra] ?: 0.0
                val overallPercentileWhip           = playerRow[SeasonGradesReliefPitchersTable.percentileWhip] ?: 0.0
                val overallPercentileKsPerNine      = playerRow[SeasonGradesReliefPitchersTable.percentileKsPerNine] ?: 0.0
                val era                             = playerRow[SeasonStatsPitchingTable.era] ?: 0.0
                val whip                            = playerRow[SeasonStatsPitchingTable.whip] ?: 0.0
                val ksPerNine                       = playerRow[SeasonStatsPitchingTable.ksPerNine] ?: 0.0
                val saves                           = playerRow[SeasonStatsPitchingTable.saves] ?: 0
                val holds                           = playerRow[SeasonStatsPitchingTable.holds] ?: 0

                playerSummaries.add(FantasyPlayerSummaryReliefPitching(playerId, firstName, lastName, currentPosition, currentTeam, overallPercentile,
                        qualifiedPercentile, overallPercentileSavesAndHolds, overallPercentileEra, overallPercentileWhip, overallPercentileKsPerNine,
                        saves, holds, era, whip, ksPerNine))
            }

        return playerSummaries
    }

    fun getBestAvailableBattersOverallFromEspn(rosteredPlayers: List<Int>, season: Int): MutableList<SeasonRankedBatter> {
        val rankedBattersList = mutableListOf<SeasonRankedBatter>()

        DatabaseConnection.database.from(BiosTable)
            .innerJoin(SeasonStatsHittingTable, on = BiosTable.playerId eq SeasonStatsHittingTable.playerId)
            .innerJoin(SeasonGradesTable, on = BiosTable.playerId eq SeasonGradesTable.playerId)
            .select(BiosTable.playerId, BiosTable.firstName, BiosTable.lastName, BiosTable.currentTeam,
                BiosTable.currentPosition, SeasonGradesTable.percentileOverall, SeasonStatsHittingTable.runs,
                SeasonStatsHittingTable.homeRuns, SeasonStatsHittingTable.rbis, SeasonStatsHittingTable.stolenBases,
                SeasonStatsHittingTable.onBasePercentage)
            .where{ (BiosTable.espnId notInList rosteredPlayers) and (SeasonStatsHittingTable.season eq season) and (SeasonGradesTable.season eq season) }
            .orderBy(SeasonGradesTable.percentileOverall.desc())
            .limit(5)
            .forEach { playerRow ->

                val playerId            = playerRow[BiosTable.playerId] ?:""
                val firstName           = playerRow[BiosTable.firstName] ?:""
                val lastName            = playerRow[BiosTable.lastName] ?:""
                val currentTeam         = playerRow[BiosTable.currentTeam] ?:""
                val currentPosition     = playerRow[BiosTable.currentPosition] ?:""
                val overallGrade        = playerRow[SeasonGradesTable.percentileOverall] ?: 0.0
                val runs                = playerRow[SeasonStatsHittingTable.runs] ?: 0
                val homeRuns            = playerRow[SeasonStatsHittingTable.homeRuns] ?: 0
                val rbis                = playerRow[SeasonStatsHittingTable.rbis] ?: 0
                val stolenBases         = playerRow[SeasonStatsHittingTable.stolenBases] ?: 0
                val onBasePercentage    = playerRow[SeasonStatsHittingTable.onBasePercentage] ?: 0.0

                rankedBattersList.add(SeasonRankedBatter(playerId, firstName, lastName, currentTeam, currentPosition,
                    overallGrade, runs, homeRuns, rbis, stolenBases, onBasePercentage))
            }
        return rankedBattersList
    }

    private fun convertAnyToDouble(value: Any?): Double? {
        return when (value) {
            is Double -> value
            is Number -> value.toDouble()
            is String -> value.toDoubleOrNull()
            else -> null
        }
    }
}
