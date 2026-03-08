package com.advanced_baseball_stats.v2.repository.stats

import com.advanced_baseball_stats.v2.helper.AgeHelper
import com.advanced_baseball_stats.v2.model.common.GameStat
import com.advanced_baseball_stats.v2.model.game.OpposingPitcherSummary
import com.advanced_baseball_stats.v2.model.pitchers.PitcherSeasonSummary
import com.advanced_baseball_stats.v2.model.pitchers.PitcherSummary
import com.advanced_baseball_stats.v2.model.pitchers.SeasonRankedReliefPitcher
import com.advanced_baseball_stats.v2.model.pitchers.SeasonRankedStartingPitcher
import com.advanced_baseball_stats.v2.repository.stats.tables.*

import org.ktorm.dsl.*
import org.ktorm.expression.ColumnDeclaringExpression
import org.ktorm.expression.OrderByExpression
import org.ktorm.schema.ColumnDeclaring
import org.ktorm.schema.SqlType

object PlayerPitchingSql
{
    private val startingPitcherPercentileOverallColumn  = SeasonGradesStartingPitchersTable.percentileOverall.aliased("percentileOverall")
    private val reliefPitcherPercentileOverallColumn    = SeasonGradesReliefPitchersTable.percentileOverall.aliased("percentileOverall")
    private val qualityStartsColumn                     = sum(PerGameStatsPitchingTable.qualityStart).aliased("qualityStarts")
    private val eraColumn                               = ((sum(PerGameStatsPitchingTable.earnedRuns.cast(SqlType.of<Double>()!!)) * 9.0) div (sum(PerGameStatsPitchingTable.ipOuts.cast(SqlType.of<Double>()!!)) div 3.0)).aliased("era")
    private val whipColumn                              = (((sum(PerGameStatsPitchingTable.walks.cast(SqlType.of<Double>()!!))) + (sum(PerGameStatsPitchingTable.hits.cast(SqlType.of<Double>()!!)))) div (sum(PerGameStatsPitchingTable.ipOuts.cast(SqlType.of<Double>()!!)) div 3.0)).aliased("whip")
    private val ksPerNineColumn                         = ((sum(PerGameStatsPitchingTable.strikeOuts.cast(SqlType.of<Double>()!!)) * 9.0) div (sum(PerGameStatsPitchingTable.ipOuts.cast(SqlType.of<Double>()!!)) div 3.0)).aliased("ksPerNine")
    private val savesColumn                             = sum((PerGameStatsPitchingTable.pitcherId eq GamesTable.savingPitcher).cast(SqlType.of<Int>()!!)).aliased("saves")
    private val holdsColumn                             = sum(PerGameStatsPitchingTable.hold).aliased("holds")

    private val sortByToOrderBy: Map<String, OrderByExpression> = mapOf(
        "PERCENTILE_OVERALL_SP"     to SeasonGradesStartingPitchersTable.percentileOverall.desc(),
        "PERCENTILE_OVERALL_RP"     to SeasonGradesReliefPitchersTable.percentileOverall.desc(),
        "QUALITY_STARTS"            to SeasonStatsPitchingTable.qualityStarts.desc(),
        "SAVES"                     to SeasonStatsPitchingTable.saves.desc(),
        "HOLDS"                     to SeasonStatsPitchingTable.holds.desc(),
        "ERA"                       to SeasonStatsPitchingTable.era.asc(),
        "WHIP"                      to SeasonStatsPitchingTable.whip.asc(),
        "KS_PER_NINE"               to SeasonStatsPitchingTable.ksPerNine.desc()
    )

    private val sortByToRankingColumnInDateRange: Map<String, OrderByExpression> = mapOf(
        "PERCENTILE_OVERALL_SP"     to startingPitcherPercentileOverallColumn.desc(),
        "PERCENTILE_OVERALL_RP"     to reliefPitcherPercentileOverallColumn.desc(),
        "QUALITY_STARTS"            to qualityStartsColumn.desc(),
        "SAVES"                     to savesColumn.desc(),
        "HOLDS"                     to holdsColumn.desc(),
        "ERA"                       to eraColumn.asc(),
        "WHIP"                      to whipColumn.asc(),
        "KS_PER_NINE"               to ksPerNineColumn.desc()
    )


    private val statToPerGameColumn: Map<String, ColumnDeclaringExpression<*>> = mapOf(
        "QUALITY_STARTS"    to PerGameStatsPitchingTable.qualityStart.aliased("qualityStarts"),
        "SAVES"             to ((PerGameStatsPitchingTable.pitcherId eq GamesTable.savingPitcher).cast(SqlType.of<Int>()!!)).aliased("saves"),
        "HOLDS"             to PerGameStatsPitchingTable.hold.aliased("qualityStarts"),
        "ERA"               to PerGameStatsPitchingTable.era.aliased("qualityStarts"),
        "WHIP"              to PerGameStatsPitchingTable.whip.aliased("qualityStarts"),
        "KS_PER_NINE"       to PerGameStatsPitchingTable.ksPerNine.aliased("qualityStarts")
    )

    private fun getSavesColumn() = sum((PerGameStatsPitchingTable.pitcherId eq GamesTable.savingPitcher).cast(SqlType.of<Int>()!!)).aliased("saves")

    fun getPlayerSummary(playerId: String): PitcherSummary?
    {
        var pitcherSummary: PitcherSummary? = null

        DatabaseConnection.database.from(BiosTable)
            .select(BiosTable.firstName, BiosTable.lastName, BiosTable.dob, BiosTable.batSide, BiosTable.throwHand,
                BiosTable.height, BiosTable.weight, BiosTable.currentTeam)
            .where{ BiosTable.playerId eq playerId }
            .limit(1)
            .forEach { playerRow ->
                val firstName                       = playerRow[BiosTable.firstName] ?: ""
                val lastName                        = playerRow[BiosTable.lastName] ?: ""
                val dob                             = playerRow[BiosTable.dob] ?: 0
                val batSide                         = playerRow[BiosTable.batSide] ?: ""
                val throwHand                       = playerRow[BiosTable.throwHand] ?: ""
                val height                          = playerRow[BiosTable.height] ?: ""
                val weight                          = playerRow[BiosTable.weight] ?: 0.0
                val currentTeam                     = playerRow[BiosTable.currentTeam] ?: ""

                pitcherSummary = PitcherSummary(playerId, firstName, lastName, AgeHelper.calculateAgeFromTimestamp(dob),
                    batSide, throwHand, height, weight, currentTeam)
            }
        return pitcherSummary
    }

    fun getSeasonSummariesForPitcher(playerId: String, startSeason: String): MutableList<PitcherSeasonSummary>
    {
        val seasonSummaries = mutableListOf<PitcherSeasonSummary>()

        DatabaseConnection.database.from(SeasonStatsPitchingTable)
            .select(
                SeasonStatsPitchingTable.season,
                SeasonStatsPitchingTable.teams,
                SeasonStatsPitchingTable.ipOuts,
                SeasonStatsPitchingTable.battersFaced,
                SeasonStatsPitchingTable.hits,
                SeasonStatsPitchingTable.homeRuns,
                SeasonStatsPitchingTable.runs,
                SeasonStatsPitchingTable.earnedRuns,
                SeasonStatsPitchingTable.walks,
                SeasonStatsPitchingTable.intentionalWalks,
                SeasonStatsPitchingTable.strikeOuts,
                SeasonStatsPitchingTable.hitByPitch,
                SeasonStatsPitchingTable.wildPitches,
                SeasonStatsPitchingTable.balks,
                SeasonStatsPitchingTable.stolenBases,
                SeasonStatsPitchingTable.caughtStealing,
                SeasonStatsPitchingTable.passedBalls,
                SeasonStatsPitchingTable.wins,
                SeasonStatsPitchingTable.losses,
                SeasonStatsPitchingTable.saves,
                SeasonStatsPitchingTable.holds,
                SeasonStatsPitchingTable.qualityStarts,
                SeasonStatsPitchingTable.gamesStarted,
                SeasonStatsPitchingTable.era,
                SeasonStatsPitchingTable.whip,
                SeasonStatsPitchingTable.ksPerNine,
                SeasonStatsPitchingTable.walksPerNine,
                SeasonStatsPitchingTable.homeRunsPerNine,
                SeasonStatsPitchingTable.averageFastballVelocity,
                SeasonStatsPitchingTable.averageExitVelocity,
                SeasonStatsPitchingTable.zonePercentage,
                SeasonStatsPitchingTable.chasePercentage,
                SeasonStatsPitchingTable.swingingStrikePercentage,
                SeasonStatsPitchingTable.hardHitPercentage,
                SeasonStatsPitchingTable.barrelPercentage,
                SeasonStatsPitchingTable.groundBallPercentage,
                SeasonStatsPitchingTable.flyBallPercentage,
                SeasonStatsPitchingTable.lineDrivePercentage,
                SeasonStatsPitchingTable.popUpPercentage,
                SeasonStatsPitchingTable.strikeOutPercentage,
                SeasonStatsPitchingTable.walkPercentage,
                SeasonStatsPitchingTable.strikeOutWalkDifference,
                SeasonStatsPitchingTable.homeRunPerFlyBallPercentage
            )
            .where { (SeasonStatsPitchingTable.season greaterEq startSeason.toInt()) and (SeasonStatsPitchingTable.playerId eq playerId) }
            .forEach { seasonRow ->
                val season                      = seasonRow[SeasonStatsPitchingTable.season] ?: -1
                val teams                       = seasonRow[SeasonStatsPitchingTable.teams] ?: ""
                val ipOuts                      = seasonRow[SeasonStatsPitchingTable.ipOuts] ?: 0
                val battersFaced                = seasonRow[SeasonStatsPitchingTable.battersFaced] ?: 0
                val hits                        = seasonRow[SeasonStatsPitchingTable.hits] ?: 0
                val homeRuns                    = seasonRow[SeasonStatsPitchingTable.homeRuns] ?: 0
                val runs                        = seasonRow[SeasonStatsPitchingTable.runs] ?: 0
                val earnedRuns                  = seasonRow[SeasonStatsPitchingTable.earnedRuns] ?: 0
                val walks                       = seasonRow[SeasonStatsPitchingTable.walks] ?: 0
                val intentionalWalks            = seasonRow[SeasonStatsPitchingTable.intentionalWalks] ?: 0
                val strikeOuts                  = seasonRow[SeasonStatsPitchingTable.strikeOuts] ?: 0
                val hitByPitch                  = seasonRow[SeasonStatsPitchingTable.hitByPitch] ?: 0
                val wildPitches                 = seasonRow[SeasonStatsPitchingTable.wildPitches] ?: 0
                val balks                       = seasonRow[SeasonStatsPitchingTable.balks] ?: 0
                val stolenBases                 = seasonRow[SeasonStatsPitchingTable.stolenBases] ?: 0
                val caughtStealing              = seasonRow[SeasonStatsPitchingTable.caughtStealing] ?: 0
                val passedBalls                 = seasonRow[SeasonStatsPitchingTable.passedBalls] ?: 0
                val wins                        = seasonRow[SeasonStatsPitchingTable.wins] ?: 0
                val losses                      = seasonRow[SeasonStatsPitchingTable.losses] ?: 0
                val saves                       = seasonRow[SeasonStatsPitchingTable.saves] ?: 0
                val holds                       = seasonRow[SeasonStatsPitchingTable.holds] ?: 0
                val qualityStarts               = seasonRow[SeasonStatsPitchingTable.qualityStarts] ?: 0
                val gamesStarted                = seasonRow[SeasonStatsPitchingTable.gamesStarted] ?: 0
                val era                         = seasonRow[SeasonStatsPitchingTable.era] ?: 0.0
                val whip                        = seasonRow[SeasonStatsPitchingTable.whip] ?: 0.0
                val ksPerNine                   = seasonRow[SeasonStatsPitchingTable.ksPerNine] ?: 0.0
                val walksPerNine                = seasonRow[SeasonStatsPitchingTable.walksPerNine] ?: 0.0
                val homeRunsPerNine             = seasonRow[SeasonStatsPitchingTable.homeRunsPerNine] ?: 0.0
                val averageFastballVelocity     = seasonRow[SeasonStatsPitchingTable.averageFastballVelocity] ?: 0.0
                val averageExitVelocity         = seasonRow[SeasonStatsPitchingTable.averageExitVelocity] ?: 0.0
                val zonePercentage              = seasonRow[SeasonStatsPitchingTable.zonePercentage] ?: 0.0
                val chasePercentage             = seasonRow[SeasonStatsPitchingTable.chasePercentage] ?: 0.0
                val swingingStrikePercentage    = seasonRow[SeasonStatsPitchingTable.swingingStrikePercentage] ?: 0.0
                val hardHitPercentage           = seasonRow[SeasonStatsPitchingTable.hardHitPercentage] ?: 0.0
                val barrelPercentage            = seasonRow[SeasonStatsPitchingTable.barrelPercentage] ?: 0.0
                val groundBallPercentage        = seasonRow[SeasonStatsPitchingTable.groundBallPercentage] ?: 0.0
                val flyBallPercentage           = seasonRow[SeasonStatsPitchingTable.flyBallPercentage] ?: 0.0
                val lineDrivePercentage         = seasonRow[SeasonStatsPitchingTable.lineDrivePercentage] ?: 0.0
                val popUpPercentage             = seasonRow[SeasonStatsPitchingTable.popUpPercentage] ?: 0.0
                val strikeOutPercentage         = seasonRow[SeasonStatsPitchingTable.strikeOutPercentage] ?: 0.0
                val walkPercentage              = seasonRow[SeasonStatsPitchingTable.walkPercentage] ?: 0.0
                val strikeOutWalkDifference     = seasonRow[SeasonStatsPitchingTable.strikeOutWalkDifference] ?: 0.0
                val homeRunPerFlyBallPercentage = seasonRow[SeasonStatsPitchingTable.homeRunPerFlyBallPercentage] ?: 0.0

                seasonSummaries.add(PitcherSeasonSummary(season, teams, ipOuts, battersFaced, hits, homeRuns, runs, earnedRuns, walks,
                    intentionalWalks, strikeOuts, hitByPitch, wildPitches, balks, stolenBases, caughtStealing, passedBalls, wins, losses,
                    saves, holds, qualityStarts, gamesStarted, era, whip, ksPerNine, walksPerNine, homeRunsPerNine, averageFastballVelocity,
                    averageExitVelocity, zonePercentage, chasePercentage, swingingStrikePercentage, hardHitPercentage, barrelPercentage,
                    groundBallPercentage, flyBallPercentage, lineDrivePercentage, popUpPercentage, strikeOutPercentage, walkPercentage,
                    strikeOutWalkDifference, homeRunPerFlyBallPercentage))
            }
        return seasonSummaries
    }

    fun getStartingPitchersRankedByStat(season: Int, sortBy: String, espnIdFilter: MutableList<Int>, limit: Int, page: Int): MutableList<SeasonRankedStartingPitcher>
    {
        val rankedStartingPitchers = mutableListOf<SeasonRankedStartingPitcher>()

        val offset = page * limit

        var convertedSortBy = sortBy

        if (convertedSortBy.equals("PERCENTILE_OVERALL"))
        {
            convertedSortBy = "PERCENTILE_OVERALL_SP"
        }

        val orderExpression = sortByToOrderBy[convertedSortBy] ?: SeasonGradesStartingPitchersTable.percentileOverall.desc()

        DatabaseConnection.database.from(SeasonStatsPitchingTable)
            .innerJoin(BiosTable, on = BiosTable.playerId eq SeasonStatsPitchingTable.playerId)
            .innerJoin(SeasonGradesStartingPitchersTable, on = SeasonGradesStartingPitchersTable.playerId eq SeasonStatsPitchingTable.playerId)
            .select(BiosTable.playerId, BiosTable.firstName, BiosTable.lastName, BiosTable.currentTeam, SeasonGradesStartingPitchersTable.percentileOverall,
                SeasonStatsPitchingTable.qualityStarts, SeasonStatsPitchingTable.era, SeasonStatsPitchingTable.whip, SeasonStatsPitchingTable.ksPerNine)
            .whereWithConditions{
                it += SeasonStatsPitchingTable.season eq season
                it += SeasonGradesStartingPitchersTable.season eq season

                if (espnIdFilter.isNotEmpty())
                {
                    it += BiosTable.espnId notInList espnIdFilter
                }
            }
            .orderBy(orderExpression)
            .limit(limit)
            .offset(offset)
            .forEach { pitcherRow ->
                val playerId            = pitcherRow[BiosTable.playerId                                 ] ?: ""
                val firstName           = pitcherRow[BiosTable.firstName                                ] ?: ""
                val lastName            = pitcherRow[BiosTable.lastName                                 ] ?: ""
                val team                = pitcherRow[BiosTable.currentTeam                              ] ?: ""
                val percentileOverall   = pitcherRow[SeasonGradesStartingPitchersTable.percentileOverall] ?: 0.0
                val qualityStarts       = pitcherRow[SeasonStatsPitchingTable.qualityStarts             ] ?: 0
                val era                 = pitcherRow[SeasonStatsPitchingTable.era                       ] ?: 0.0
                val whip                = pitcherRow[SeasonStatsPitchingTable.whip                      ] ?: 0.0
                val ksPerNine           = pitcherRow[SeasonStatsPitchingTable.ksPerNine                 ] ?: 0.0

                rankedStartingPitchers.add(SeasonRankedStartingPitcher(playerId, firstName, lastName, team, percentileOverall, qualityStarts, era, whip, ksPerNine))
            }
        return rankedStartingPitchers
    }

    fun getStartingPitcherRankedByStatInDateRange(season: Int, sortBy: String, startDate: String, endDate: String, espnIdFilter: MutableList<Int>, limit: Int, page: Int): MutableList<SeasonRankedStartingPitcher>
    {
        val rankedStartingPitcherList = mutableListOf<SeasonRankedStartingPitcher>()

        val offset = page * limit

        val dateRanges: ClosedRange<String> = startDate..endDate

        var query = DatabaseConnection.database.from(PerGameStatsPitchingTable)
            .innerJoin(GamesTable, on = GamesTable.gameId eq PerGameStatsPitchingTable.gameId)
            .innerJoin(BiosTable, on = BiosTable.playerId eq PerGameStatsPitchingTable.pitcherId)
            .innerJoin(SeasonGradesStartingPitchersTable, on = SeasonGradesStartingPitchersTable.playerId eq PerGameStatsPitchingTable.pitcherId)
            .select(PerGameStatsPitchingTable.pitcherId, BiosTable.firstName, BiosTable.lastName, BiosTable.currentTeam,
                startingPitcherPercentileOverallColumn, qualityStartsColumn, eraColumn, whipColumn, ksPerNineColumn)
            .whereWithConditions {
                it += GamesTable.date between dateRanges
                it += SeasonGradesStartingPitchersTable.season eq season

                if (espnIdFilter.isNotEmpty())
                {
                    it += BiosTable.espnId notInList espnIdFilter
                }
            }
            .limit(limit)
            .offset(offset)
            .groupBy(PerGameStatsPitchingTable.pitcherId)

            var convertedSortBy = sortBy

            if (convertedSortBy.equals("OVERALL_PERCENTILE"))
            {
                convertedSortBy = "OVERALL_PERCENTILE_SP"
            }

            if (sortByToRankingColumnInDateRange.containsKey(convertedSortBy))
            {
                query = query.orderBy(sortByToRankingColumnInDateRange[convertedSortBy]!!)
            }

            query.forEach { playerRow ->
                val playerId            = playerRow[PerGameStatsPitchingTable.pitcherId] ?: ""
                val firstName           = playerRow[BiosTable.firstName] ?: ""
                val lastName            = playerRow[BiosTable.lastName] ?: ""
                val currentTeam         = playerRow[BiosTable.currentTeam] ?: ""
                val percentileOverall   = playerRow[startingPitcherPercentileOverallColumn] ?: 0.0
                val qualityStarts       = playerRow[qualityStartsColumn] ?: 0
                val era                 = playerRow[eraColumn] ?: 0.0
                val whip                = playerRow[whipColumn] ?: 0.0
                val ksPerNine           = playerRow[ksPerNineColumn] ?: 0.0

                rankedStartingPitcherList.add(SeasonRankedStartingPitcher(playerId, firstName, lastName, currentTeam, percentileOverall, qualityStarts,
                    era, whip, ksPerNine))
            }
        return rankedStartingPitcherList
    }

    fun getReliefPitchersRankedByStat(season: Int, sortBy: String, espnIdFilter: MutableList<Int>, limit: Int, page: Int): MutableList<SeasonRankedReliefPitcher>
    {
        val rankedReliefPitchers = mutableListOf<SeasonRankedReliefPitcher>()

        val offset = page * limit

        var convertedSortBy = sortBy

        if (convertedSortBy.equals("PERCENTILE_OVERALL"))
        {
            convertedSortBy = "PERCENTILE_OVERALL_RP"
        }

        val orderExpression = sortByToOrderBy[convertedSortBy] ?: SeasonGradesStartingPitchersTable.percentileOverall.desc()

        DatabaseConnection.database.from(SeasonStatsPitchingTable)
            .innerJoin(BiosTable, on = BiosTable.playerId eq SeasonStatsPitchingTable.playerId)
            .innerJoin(SeasonGradesReliefPitchersTable, on = SeasonGradesReliefPitchersTable.playerId eq SeasonStatsPitchingTable.playerId)
            .select(BiosTable.playerId, BiosTable.firstName, BiosTable.lastName, BiosTable.currentTeam, SeasonGradesReliefPitchersTable.percentileOverall,
                SeasonStatsPitchingTable.saves, SeasonStatsPitchingTable.holds, SeasonStatsPitchingTable.era, SeasonStatsPitchingTable.whip, SeasonStatsPitchingTable.ksPerNine)
            .whereWithConditions{
                it += SeasonStatsPitchingTable.season eq season
                it += SeasonGradesReliefPitchersTable.season eq season

                if (espnIdFilter.isNotEmpty())
                {
                    it += BiosTable.espnId notInList espnIdFilter
                }
            }
            .orderBy(orderExpression)
            .limit(limit)
            .offset(offset)
            .forEach { pitcherRow ->
                val playerId            = pitcherRow[BiosTable.playerId                                 ] ?: ""
                val firstName           = pitcherRow[BiosTable.firstName                                ] ?: ""
                val lastName            = pitcherRow[BiosTable.lastName                                 ] ?: ""
                val team                = pitcherRow[BiosTable.currentTeam                              ] ?: ""
                val percentileOverall   = pitcherRow[SeasonGradesReliefPitchersTable.percentileOverall  ] ?: 0.0
                val saves               = pitcherRow[SeasonStatsPitchingTable.saves                     ] ?: 0
                val holds               = pitcherRow[SeasonStatsPitchingTable.holds                     ] ?: 0
                val era                 = pitcherRow[SeasonStatsPitchingTable.era                       ] ?: 0.0
                val whip                = pitcherRow[SeasonStatsPitchingTable.whip                      ] ?: 0.0
                val ksPerNine           = pitcherRow[SeasonStatsPitchingTable.ksPerNine                 ] ?: 0.0

                rankedReliefPitchers.add(SeasonRankedReliefPitcher(playerId, firstName, lastName, team, percentileOverall, saves, holds, era, whip, ksPerNine))
            }
        return rankedReliefPitchers
    }

    fun getReliefPitcherRankedByStatInDateRange(season: Int, sortBy: String, startDate: String, endDate: String, espnIdFilter: MutableList<Int>, limit: Int, page: Int): MutableList<SeasonRankedReliefPitcher>
    {
        val rankedReliefPitcherList = mutableListOf<SeasonRankedReliefPitcher>()

        val offset = page * limit

        val dateRanges: ClosedRange<String> = startDate..endDate

        var query = DatabaseConnection.database.from(PerGameStatsPitchingTable)
            .innerJoin(GamesTable, on = GamesTable.gameId eq PerGameStatsPitchingTable.gameId)
            .innerJoin(BiosTable, on = BiosTable.playerId eq PerGameStatsPitchingTable.pitcherId)
            .innerJoin(SeasonGradesReliefPitchersTable, on = SeasonGradesReliefPitchersTable.playerId eq PerGameStatsPitchingTable.pitcherId)
            .select(PerGameStatsPitchingTable.pitcherId, BiosTable.firstName, BiosTable.lastName, BiosTable.currentTeam,
                reliefPitcherPercentileOverallColumn, getSavesColumn(), holdsColumn, eraColumn, whipColumn, ksPerNineColumn)
            .whereWithConditions {
                it += GamesTable.date between dateRanges
                it += SeasonGradesReliefPitchersTable.season eq season

                if (espnIdFilter.isNotEmpty())
                {
                    it += BiosTable.espnId notInList espnIdFilter
                }
            }
            .limit(limit)
            .offset(offset)
            .groupBy(PerGameStatsPitchingTable.pitcherId)

        var convertedSortBy = sortBy

        if (convertedSortBy.equals("PERCENTILE_OVERALL"))
        {
            convertedSortBy = "PERCENTILE_OVERALL_RP"
        }

        if (sortByToRankingColumnInDateRange.containsKey(convertedSortBy))
        {
            query = query.orderBy(sortByToRankingColumnInDateRange[convertedSortBy]!!)
        }

        query.forEach { playerRow ->
            val playerId            = playerRow[PerGameStatsPitchingTable.pitcherId ] ?: ""
            val firstName           = playerRow[BiosTable.firstName                 ] ?: ""
            val lastName            = playerRow[BiosTable.lastName                  ] ?: ""
            val currentTeam         = playerRow[BiosTable.currentTeam               ] ?: ""
            val percentileOverall   = playerRow[reliefPitcherPercentileOverallColumn] ?: 0.0
            val saves               = playerRow[getSavesColumn()                    ] ?: 0
            val holds               = playerRow[holdsColumn                         ] ?: 0
            val era                 = playerRow[eraColumn                           ] ?: 0.0
            val whip                = playerRow[whipColumn                          ] ?: 0.0
            val ksPerNine           = playerRow[ksPerNineColumn                     ] ?: 0.0

            rankedReliefPitcherList.add(SeasonRankedReliefPitcher(playerId, firstName, lastName, currentTeam, percentileOverall, saves, holds,
                era, whip, ksPerNine))
        }
        return rankedReliefPitcherList
    }

    fun getBestAvailableStartingPitchersFromEspn(rosteredPlayers: List<Int>, season: Int): MutableList<SeasonRankedStartingPitcher>
    {
        val rankedStartingPitcherList: MutableList<SeasonRankedStartingPitcher> = mutableListOf()

        DatabaseConnection.database.from(BiosTable)
            .innerJoin(SeasonStatsPitchingTable, on = BiosTable.playerId eq SeasonStatsPitchingTable.playerId)
            .innerJoin(SeasonGradesStartingPitchersTable, on = BiosTable.playerId eq SeasonGradesStartingPitchersTable.playerId)
            .select(BiosTable.playerId, BiosTable.firstName, BiosTable.lastName, BiosTable.currentTeam,
                SeasonGradesStartingPitchersTable.percentileOverall, SeasonStatsPitchingTable.qualityStarts,
                SeasonStatsPitchingTable.era, SeasonStatsPitchingTable.whip, SeasonStatsPitchingTable.ksPerNine)
            .where { (BiosTable.espnId notInList rosteredPlayers) and (SeasonStatsPitchingTable.season eq season) and (SeasonGradesStartingPitchersTable.season eq season) }
            .orderBy(SeasonGradesStartingPitchersTable.percentileOverall.desc())
            .limit(5)
            .forEach { playerRow ->
                val playerId            = playerRow[BiosTable.playerId] ?: ""
                val firstName           = playerRow[BiosTable.firstName] ?: ""
                val lastName            = playerRow[BiosTable.lastName] ?: ""
                val currentTeam         = playerRow[BiosTable.currentTeam] ?: ""
                val overallGrade        = playerRow[SeasonGradesStartingPitchersTable.percentileOverall] ?: 0.0
                val qualityStarts       = playerRow[SeasonStatsPitchingTable.qualityStarts] ?: 0
                val era                 = playerRow[SeasonStatsPitchingTable.era] ?: 0.0
                val whip                = playerRow[SeasonStatsPitchingTable.whip] ?: 0.0
                val ksPerNine           = playerRow[SeasonStatsPitchingTable.ksPerNine] ?: 0.0

                rankedStartingPitcherList.add(SeasonRankedStartingPitcher(playerId, firstName, lastName, currentTeam, overallGrade,
                    qualityStarts, era, whip, ksPerNine))
        }

        return rankedStartingPitcherList
    }

    fun getBestAvailableReliefPitchersFromEspn(rosteredPlayers: List<Int>, season: Int): MutableList<SeasonRankedReliefPitcher>
    {
        val rankedReliefPitcherList: MutableList<SeasonRankedReliefPitcher> = mutableListOf()

        DatabaseConnection.database.from(BiosTable)
            .innerJoin(SeasonStatsPitchingTable, on = BiosTable.playerId eq SeasonStatsPitchingTable.playerId)
            .innerJoin(SeasonGradesReliefPitchersTable, on = BiosTable.playerId eq SeasonGradesReliefPitchersTable.playerId)
            .select(BiosTable.playerId, BiosTable.firstName, BiosTable.lastName, BiosTable.currentTeam,
                BiosTable.currentPosition, SeasonGradesReliefPitchersTable.percentileOverall, SeasonStatsPitchingTable.saves,
                SeasonStatsPitchingTable.holds, SeasonStatsPitchingTable.era, SeasonStatsPitchingTable.whip, SeasonStatsPitchingTable.ksPerNine)
            .where{ (BiosTable.espnId notInList rosteredPlayers) and (SeasonStatsPitchingTable.season eq season) and (SeasonGradesReliefPitchersTable.season eq season) }
            .orderBy(SeasonGradesReliefPitchersTable.percentileOverall.desc())
            .limit(5)
            .forEach { playerRow ->
                val playerId            = playerRow[BiosTable.playerId] ?: ""
                val firstName           = playerRow[BiosTable.firstName] ?: ""
                val lastName            = playerRow[BiosTable.lastName] ?: ""
                val currentTeam         = playerRow[BiosTable.currentTeam] ?: ""
                val currentPosition     = playerRow[BiosTable.currentPosition] ?: ""
                val overallGrade        = playerRow[SeasonGradesReliefPitchersTable.percentileOverall] ?: 0.0
                val saves               = playerRow[SeasonStatsPitchingTable.saves] ?: 0
                val holds               = playerRow[SeasonStatsPitchingTable.holds] ?: 0
                val era                 = playerRow[SeasonStatsPitchingTable.era] ?: 0.0
                val whip                = playerRow[SeasonStatsPitchingTable.whip] ?: 0.0
                val ksPerNine           = playerRow[SeasonStatsPitchingTable.ksPerNine] ?: 0.0

                rankedReliefPitcherList.add(SeasonRankedReliefPitcher(playerId, firstName, lastName, currentPosition, overallGrade,
                    saves, holds, era, whip, ksPerNine))
            }
        return rankedReliefPitcherList
    }

    fun getOpposingPitcherSummary(pitcherId: String, season: Int): OpposingPitcherSummary?
    {
        DatabaseConnection.database.from(SeasonGradesStartingPitchersTable)
            .innerJoin(BiosTable, on = BiosTable.playerId eq pitcherId)
            .select(BiosTable.firstName, BiosTable.lastName, SeasonGradesStartingPitchersTable.percentileOverall)
            .where{ (SeasonGradesStartingPitchersTable.playerId eq pitcherId) and (SeasonGradesStartingPitchersTable.season eq season)}
            .forEachIndexed { index, pitcherRow ->
                if (index == 0)
                {
                    val firstName           = pitcherRow[BiosTable.firstName                                ] ?: ""
                    val lastName            = pitcherRow[BiosTable.lastName                                 ] ?: ""
                    val percentileOverall   = pitcherRow[SeasonGradesStartingPitchersTable.percentileOverall] ?: 0.0

                    return OpposingPitcherSummary(pitcherId, firstName, lastName, percentileOverall)
                }
            }
        return null
    }

    fun getPitchingStatPerGame(playerId: String, season: Int, stat: String): MutableList<GameStat>
    {
        val pitcherGames = mutableListOf<GameStat>()

        val statColumn = statToPerGameColumn[stat] ?: PerGameStatsPitchingTable.era.aliased("era")

        DatabaseConnection.database.from(PerGameStatsPitchingTable)
            .innerJoin(GamesTable, on = PerGameStatsPitchingTable.gameId eq GamesTable.gameId)
            .select(GamesTable.gameId, GamesTable.date, GamesTable.homeTeam, GamesTable.visTeam, statColumn)
            .where { (GamesTable.season eq season) and (PerGameStatsPitchingTable.pitcherId eq playerId) }
            .forEach { gameRow ->
                val gameId      = gameRow[GamesTable.gameId]    ?: ""
                val date        = gameRow[GamesTable.date]      ?: ""
                val homeTeam    = gameRow[GamesTable.homeTeam]  ?: ""
                val visTeam     = gameRow[GamesTable.visTeam]   ?: ""
                val gameStat    = gameRow[statColumn]           ?: 0.0

                val convertedGameStat = convertAnyToDouble(gameStat) ?: 0.0

                pitcherGames.add(GameStat(gameId, date, homeTeam, visTeam, convertedGameStat))
            }
        return pitcherGames
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