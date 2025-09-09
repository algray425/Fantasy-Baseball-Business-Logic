package com.advanced_baseball_stats.repository

import com.advanced_baseball_stats.model.common.Team
import com.advanced_baseball_stats.model.database.PlayerPitching
import com.advanced_baseball_stats.model.game.*
import com.advanced_baseball_stats.model.pitching.HolisticPitchingStatList
import com.advanced_baseball_stats.model.pitching.PitchingGame
import com.advanced_baseball_stats.model.pitching.PitchingGameStat
import com.advanced_baseball_stats.model.pitching.PitchingStat
import com.advanced_baseball_stats.repository.tables.GameTable
import com.advanced_baseball_stats.repository.tables.PitcherTable
import org.ktorm.dsl.*
import org.ktorm.schema.Column
import org.ktorm.schema.ColumnDeclaring
import org.ktorm.schema.SqlType
import java.math.RoundingMode

object PlayerPitchingSql
{
    private val columnToPitchingStat: Map<Column<Int>, PitchingStat> = mapOf(
            PitcherTable.p_er       to PitchingStat.EARNED_RUNS
        ,   PitcherTable.p_ipouts   to PitchingStat.OUTS
        ,   PitcherTable.p_r        to PitchingStat.RUNS
        ,   PitcherTable.p_h        to PitchingStat.HITS
        ,   PitcherTable.p_d        to PitchingStat.DOUBLES
        ,   PitcherTable.p_t        to PitchingStat.TRIPLES
        ,   PitcherTable.p_hr       to PitchingStat.HOME_RUNS
        ,   PitcherTable.p_h        to PitchingStat.HITS
        ,   PitcherTable.p_w        to PitchingStat.WALKS
        ,   PitcherTable.p_k        to PitchingStat.STRIKE_OUTS
    )

    private val pitchingStatToColumn: Map<PitchingStat, Column<Int>> = mapOf(
            PitchingStat.EARNED_RUNS    to PitcherTable.p_er
        ,   PitchingStat.OUTS           to PitcherTable.p_ipouts
        ,   PitchingStat.RUNS           to PitcherTable.p_r
        ,   PitchingStat.HITS           to PitcherTable.p_h
        ,   PitchingStat.DOUBLES        to PitcherTable.p_d
        ,   PitchingStat.TRIPLES        to PitcherTable.p_t
        ,   PitchingStat.HOME_RUNS      to PitcherTable.p_hr
        ,   PitchingStat.HITS           to PitcherTable.p_h
        ,   PitchingStat.WALKS          to PitcherTable.p_w
        ,   PitchingStat.STRIKE_OUTS    to PitcherTable.p_k
    )

    private val pitchingStatToColumnsNeeded: Map<PitchingStat, List<ColumnDeclaring<*>>> = mapOf(
            PitchingStat.HITS                   to listOf(PitcherTable.p_h)
        ,   PitchingStat.SINGLES                to listOf(PitcherTable.p_h, PitcherTable.p_d, PitcherTable.p_t, PitcherTable.p_hr)
        ,   PitchingStat.DOUBLES                to listOf(PitcherTable.p_d)
        ,   PitchingStat.TRIPLES                to listOf(PitcherTable.p_t)
        ,   PitchingStat.HOME_RUNS              to listOf(PitcherTable.p_hr)
        ,   PitchingStat.RUNS                   to listOf(PitcherTable.p_r)
        ,   PitchingStat.EARNED_RUNS            to listOf(PitcherTable.p_er)
        ,   PitchingStat.WALKS                  to listOf(PitcherTable.p_w)
        ,   PitchingStat.STRIKE_OUTS            to listOf(PitcherTable.p_k)
        ,   PitchingStat.OUTS                   to listOf(PitcherTable.p_ipouts)
        ,   PitchingStat.ERA                    to listOf(PitcherTable.p_er, PitcherTable.p_ipouts)
        ,   PitchingStat.WHIP                   to listOf(PitcherTable.p_h, PitcherTable.p_w, PitcherTable.p_ipouts)
        ,   PitchingStat.STRIKE_OUTS_PER_NINE   to listOf(PitcherTable.p_k, PitcherTable.p_ipouts)
        ,   PitchingStat.QUALITY_START          to listOf(PitcherTable.p_er, PitcherTable.p_ipouts)
    )

    private val pitchingStatToAggregatePitchingExtractor: Map<PitchingStat, (QueryRowSet, MutableMap<PitchingStat, Double>, MutableMap<PitchingStat, Boolean>)->Double> = mapOf(

            PitchingStat.HITS                   to ::extractPitchingHitsAggregateFromRow
        ,   PitchingStat.SINGLES                to ::extractPitchingSinglesAggregateFromRow
        ,   PitchingStat.DOUBLES                to ::extractPitchingDoublesAggregateFromRow
        ,   PitchingStat.TRIPLES                to ::extractPitchingTriplesAggregateFromRow
        ,   PitchingStat.HOME_RUNS              to ::extractPitchingHomeRunsAggregateFromRow
        ,   PitchingStat.RUNS                   to ::extractPitchingRunsAggregateFromRow
        ,   PitchingStat.EARNED_RUNS            to ::extractPitchingEarnedRunsAggregateFromRow
        ,   PitchingStat.WALKS                  to ::extractPitchingWalksAggregateFromRow
        ,   PitchingStat.STRIKE_OUTS            to ::extractPitchingStrikeOutsAggregateFromRow
        ,   PitchingStat.OUTS                   to ::extractPitchingOutsAggregateFromRow
        ,   PitchingStat.HITS                   to ::extractPitchingHitsAggregateFromRow
        ,   PitchingStat.ERA                    to ::extractEraAggregateFromRow
        ,   PitchingStat.WHIP                   to ::extractWhipAggregateGameFromRow
        ,   PitchingStat.STRIKE_OUTS_PER_NINE   to ::extractStrikeOutsPerNineAggregateFromRow
        ,   PitchingStat.QUALITY_START          to ::extractQualityStartsAggregateFromRow
    )

    private val pitchingStatToPerGamePitchingExtractor: Map<PitchingStat, (QueryRowSet) -> Double> = mapOf(
            PitchingStat.HITS                   to ::extractPitchingHitsPerGameFromRow
        ,   PitchingStat.SINGLES                to ::extractPitchingSinglesPerGameFromRow
        ,   PitchingStat.DOUBLES                to ::extractPitchingDoublesPerGameFromRow
        ,   PitchingStat.TRIPLES                to ::extractPitchingTriplesPerGameFromRow
        ,   PitchingStat.HOME_RUNS              to ::extractPitchingHomeRunsPerGameFromRow
        ,   PitchingStat.RUNS                   to ::extractPitchingRunsPerGameFromRow
        ,   PitchingStat.EARNED_RUNS            to ::extractEarnedRunsPerGameFromRow
        ,   PitchingStat.WALKS                  to ::extractPitchingWalksPerGameFromRow
        ,   PitchingStat.STRIKE_OUTS            to ::extractPitchingStrikeOutsPerGameFromRow
        ,   PitchingStat.OUTS                   to ::extractPitchingOutsPerGameFromRow
        ,   PitchingStat.ERA                    to ::extractEraPerGameFromRow
        ,   PitchingStat.WHIP                   to ::extractWhipPerGameFromRow
        ,   PitchingStat.STRIKE_OUTS_PER_NINE   to ::extractStrikeOutsPerNinePerGameFromRow
        ,   PitchingStat.QUALITY_START          to ::extractQualityStartsPerGameFromRow
    )

    private val gameColumns = listOf(
        GameTable.gid, GameTable.temp, GameTable.sky, GameTable.winddir, GameTable.windspeed, GameTable.precip,
        GameTable.daynight, GameTable.hometeam, GameTable.visteam, GameTable.site)

    fun getTotalPlayerPitching(id: String, startDate: String, endDate: String): PlayerPitching
    {
        val dateRanges: ClosedRange<String> = startDate..endDate

        val eraColumn           = ((sum(PitcherTable.p_er.cast(SqlType.of<Double>()!!)) times 9.0) div (sum(PitcherTable.p_ipouts.cast(SqlType.of<Double>()!!)) div 3.0)).aliased("p_era")
        val whipColumn          = (((sum(PitcherTable.p_w.cast(SqlType.of<Double>()!!))) + (sum(PitcherTable.p_h.cast(SqlType.of<Double>()!!)))) div (sum(PitcherTable.p_ipouts.cast(SqlType.of<Double>()!!)) div 3.0)).aliased("p_whip")
        val homeRunsColumn      = (sum(PitcherTable.p_hr)).aliased("homeruns"   )
        val strikeOutsColumn    = (sum(PitcherTable.p_k)) .aliased("strikeouts" )

        var era         = 0.0
        var whip        = 0.0
        var homeRuns    = 0
        var strikeOuts  = 0

        DatabaseConnection.database.from(PitcherTable)
            .select(eraColumn, whipColumn, homeRunsColumn, strikeOutsColumn)
            .where { (PitcherTable.id eq id) and (PitcherTable.date between dateRanges) }
            .limit(1)
            .forEach { row ->
                val curEra         = row[eraColumn         ]
                val curWhip        = row[whipColumn        ]
                val curHomeRuns    = row[homeRunsColumn    ]
                val curStrikeOuts  = row[strikeOutsColumn  ]

                if (curEra != null && curWhip != null && curHomeRuns != null && curStrikeOuts != null)
                {
                    era         = curEra
                    whip        = curWhip
                    homeRuns    = curHomeRuns
                    strikeOuts  = curStrikeOuts
                }
            }

        val totalPitching = PlayerPitching(id, era, whip, homeRuns, strikeOuts)

        return totalPitching
    }

    fun getPitchingStatsAggregate(id: String, startDate: String, endDate: String, statList: List<PitchingStat>): HolisticPitchingStatList
    {
        val pitchingGames = mutableListOf<PitchingGame>()

        val pitchingStatToSum   = mutableMapOf<PitchingStat, Double>()
        val columnsToSelect     = mutableListOf<ColumnDeclaring<*>>()

        for (stat in statList)
        {
            if (stat in pitchingStatToColumnsNeeded)
            {
                val columnsNeeded = pitchingStatToColumnsNeeded[stat]!!

                for (column in columnsNeeded)
                {
                    val curStat = columnToPitchingStat[column]!!

                    pitchingStatToSum[curStat] = 0.0
                }

                columnsToSelect.addAll(columnsNeeded)
            }
        }

        columnsToSelect.addAll(gameColumns)

        val dateRanges: ClosedRange<String> = startDate..endDate

        DatabaseConnection.database.from(PitcherTable)
            .innerJoin(GameTable, on = PitcherTable.gid eq GameTable.gid)
            .select(columnsToSelect)
            .where{ (PitcherTable.id eq id) and (PitcherTable.date between dateRanges)}
            .orderBy(PitcherTable.date.asc())
            .forEach { pitcherRow ->
                val curGame = this.getGame(pitcherRow)

                if (curGame != null)
                {
                    val updated = mutableMapOf<PitchingStat, Boolean>()

                    val pitchingGame = PitchingGame(curGame)

                    for (stat in statList)
                    {
                        val num = pitchingStatToAggregatePitchingExtractor[stat]?.invoke(pitcherRow, pitchingStatToSum, updated)!!

                        val pitchingGameStat = PitchingGameStat(stat, num)

                        pitchingGame.stats.add(pitchingGameStat)
                    }
                    pitchingGames.add(pitchingGame)
                }
            }
        return HolisticPitchingStatList(id, pitchingGames)
    }

    private fun extractPitchingStatAggregateFromRow(row: QueryRowSet, pitchingStat: PitchingStat, pitchingStatToSum: MutableMap<PitchingStat, Double>, updated: MutableMap<PitchingStat, Boolean>): Double
    {
        if (!updated.containsKey(pitchingStat))
        {
            val previousNum = if (pitchingStatToSum.containsKey(pitchingStat)) pitchingStatToSum[pitchingStat]!! else 0.0

            val columnToExtract = pitchingStatToColumn[pitchingStat]!!

            val curNum = row[columnToExtract]!!

            val totalNum = previousNum + curNum

            pitchingStatToSum   [pitchingStat] = totalNum
            updated             [pitchingStat] = true

            return totalNum
        }
        else
        {
            val num = pitchingStatToSum[pitchingStat]!!

            return num
        }
    }

    private fun extractPitchingEarnedRunsAggregateFromRow(row: QueryRowSet, pitchingStatToSum: MutableMap<PitchingStat, Double>, updated: MutableMap<PitchingStat, Boolean>): Double
    {
        return extractPitchingStatAggregateFromRow(row, PitchingStat.EARNED_RUNS, pitchingStatToSum, updated)
    }

    private fun extractPitchingOutsAggregateFromRow(row: QueryRowSet, pitchingStatToSum: MutableMap<PitchingStat, Double>, updated: MutableMap<PitchingStat, Boolean>): Double
    {
        return extractPitchingStatAggregateFromRow(row, PitchingStat.OUTS, pitchingStatToSum, updated)
    }

    private fun extractPitchingHitsAggregateFromRow(row: QueryRowSet, pitchingStatToSum: MutableMap<PitchingStat, Double>, updated: MutableMap<PitchingStat, Boolean>): Double
    {
        return extractPitchingStatAggregateFromRow(row, PitchingStat.HITS, pitchingStatToSum, updated)
    }

    private fun extractPitchingDoublesAggregateFromRow(row: QueryRowSet, pitchingStatToSum: MutableMap<PitchingStat, Double>, updated: MutableMap<PitchingStat, Boolean>): Double
    {
        return extractPitchingStatAggregateFromRow(row, PitchingStat.DOUBLES, pitchingStatToSum, updated)
    }

    private fun extractPitchingTriplesAggregateFromRow(row: QueryRowSet, pitchingStatToSum: MutableMap<PitchingStat, Double>, updated: MutableMap<PitchingStat, Boolean>): Double
    {
        return extractPitchingStatAggregateFromRow(row, PitchingStat.TRIPLES, pitchingStatToSum, updated)
    }

    private fun extractPitchingHomeRunsAggregateFromRow(row: QueryRowSet, pitchingStatToSum: MutableMap<PitchingStat, Double>, updated: MutableMap<PitchingStat, Boolean>): Double
    {
        return extractPitchingStatAggregateFromRow(row, PitchingStat.HOME_RUNS, pitchingStatToSum, updated)
    }

    private fun extractPitchingRunsAggregateFromRow(row: QueryRowSet, pitchingStatToSum: MutableMap<PitchingStat, Double>, updated: MutableMap<PitchingStat, Boolean>): Double
    {
        return extractPitchingStatAggregateFromRow(row, PitchingStat.RUNS, pitchingStatToSum, updated)
    }

    private fun extractPitchingWalksAggregateFromRow(row: QueryRowSet, pitchingStatToSum: MutableMap<PitchingStat, Double>, updated: MutableMap<PitchingStat, Boolean>): Double
    {
        return extractPitchingStatAggregateFromRow(row, PitchingStat.WALKS, pitchingStatToSum, updated)
    }

    private fun extractPitchingStrikeOutsAggregateFromRow(row: QueryRowSet, pitchingStatToSum: MutableMap<PitchingStat, Double>, updated: MutableMap<PitchingStat, Boolean>): Double
    {
        return extractPitchingStatAggregateFromRow(row, PitchingStat.STRIKE_OUTS, pitchingStatToSum, updated)
    }

    private fun extractPitchingSinglesAggregateFromRow(row: QueryRowSet, pitchingStatToSum: MutableMap<PitchingStat, Double>, updated: MutableMap<PitchingStat, Boolean>): Double
    {
        val hits        = extractPitchingHitsAggregateFromRow       (row, pitchingStatToSum, updated)
        val doubles     = extractPitchingDoublesAggregateFromRow    (row, pitchingStatToSum, updated)
        val triples     = extractPitchingTriplesAggregateFromRow    (row, pitchingStatToSum, updated)
        val homeRuns    = extractPitchingHomeRunsAggregateFromRow   (row, pitchingStatToSum, updated)

        val singles = hits - doubles - triples - homeRuns

        return singles
    }

    private fun extractEraAggregateFromRow(row: QueryRowSet, pitchingStatToSum: MutableMap<PitchingStat, Double>, updated: MutableMap<PitchingStat, Boolean>): Double
    {
        val earnedRuns  = extractPitchingEarnedRunsAggregateFromRow (row, pitchingStatToSum, updated)
        val outs        = extractPitchingOutsAggregateFromRow       (row, pitchingStatToSum, updated)

        val rawEra = (earnedRuns * 9.0) / (outs / 3.0)

        val roundedEra = rawEra.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toDouble()

        return roundedEra
    }

    private fun extractWhipAggregateGameFromRow(row: QueryRowSet, pitchingStatToSum: MutableMap<PitchingStat, Double>, updated: MutableMap<PitchingStat, Boolean>): Double
    {
        val hits    = extractPitchingHitsAggregateFromRow   (row, pitchingStatToSum, updated)
        val walks   = extractPitchingWalksAggregateFromRow  (row, pitchingStatToSum, updated)
        val outs    = extractPitchingOutsAggregateFromRow   (row, pitchingStatToSum, updated)

        val rawWhip = (walks + hits) / (outs / 3.0)

        val roundedWhip = rawWhip.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toDouble()

        return roundedWhip
    }

    private fun extractStrikeOutsPerNineAggregateFromRow(row: QueryRowSet, pitchingStatToSum: MutableMap<PitchingStat, Double>, updated: MutableMap<PitchingStat, Boolean>): Double
    {
        val strikeOuts  = extractPitchingStrikeOutsAggregateFromRow (row, pitchingStatToSum, updated)
        val outs        = extractPitchingOutsAggregateFromRow       (row, pitchingStatToSum, updated)

        val rawStrikeOutsPerNine = (strikeOuts * 9.0) / (outs / 3.0)

        val roundedStrikeOutsPerNine = rawStrikeOutsPerNine.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toDouble()

        return roundedStrikeOutsPerNine
    }

    private fun extractQualityStartsAggregateFromRow(row: QueryRowSet, pitchingStatToSum: MutableMap<PitchingStat, Double>, updated: MutableMap<PitchingStat, Boolean>): Double
    {
        val earnedRuns  = extractEarnedRunsPerGameFromRow   (row)
        val outs        = extractPitchingOutsPerGameFromRow (row)

        val inningsPitched = outs / 3.0

        if (!updated.containsKey(PitchingStat.QUALITY_START))
        {
            val previousQualityStarts = if (pitchingStatToSum.containsKey(PitchingStat.QUALITY_START)) pitchingStatToSum[PitchingStat.QUALITY_START]!! else 0.0

            val updatedQualityStarts = if (earnedRuns <= 3.0 && inningsPitched >= 6.0) previousQualityStarts + 1 else previousQualityStarts

            pitchingStatToSum   [PitchingStat.QUALITY_START] = updatedQualityStarts
            updated             [PitchingStat.QUALITY_START] = true

            return updatedQualityStarts
        }
        else
        {
            val qualityStarts = pitchingStatToSum[PitchingStat.QUALITY_START]!!

            return qualityStarts
        }
    }

    fun getPitchingStatPerGame(id: String, startDate: String, endDate: String, statList: List<PitchingStat>): HolisticPitchingStatList
    {
        val pitchingGames = mutableListOf<PitchingGame>()

        val columnsToSelect = mutableListOf<ColumnDeclaring<*>>()

        for (stat in statList)
        {
            if (stat in pitchingStatToColumnsNeeded)
            {
                val columnsNeeded = pitchingStatToColumnsNeeded[stat]!!

                columnsToSelect.addAll(columnsNeeded)
            }
        }

        columnsToSelect.addAll(gameColumns)

        val dateRanges: ClosedRange<String> = startDate..endDate

        DatabaseConnection.database.from(PitcherTable)
            .innerJoin(GameTable, on = PitcherTable.gid eq GameTable.gid)
            .select(columnsToSelect)
            .where{ (PitcherTable.id eq id) and (PitcherTable.date between dateRanges)}
            .orderBy(PitcherTable.date.asc())
            .forEach { pitcherRow ->
                val curGame = this.getGame(pitcherRow)

                if (curGame != null)
                {
                    val pitchingGame = PitchingGame(curGame)

                    for (stat in statList)
                    {
                        val num = pitchingStatToPerGamePitchingExtractor[stat]?.invoke(pitcherRow)!!

                        val pitchingGameStat = PitchingGameStat(stat, num)

                        pitchingGame.stats.add(pitchingGameStat)
                    }
                    pitchingGames.add(pitchingGame)
                }
            }
        return HolisticPitchingStatList(id, pitchingGames)
    }

    private fun extractPitchingStatPerGameFromRow(row: QueryRowSet, stat: PitchingStat): Double
    {
        val column = pitchingStatToColumn[stat]!!

        val num = row[column]!!

        return num.toDouble()
    }

    private fun extractEarnedRunsPerGameFromRow(row: QueryRowSet): Double
    {
        return extractPitchingStatPerGameFromRow(row, PitchingStat.EARNED_RUNS)
    }

    private fun extractPitchingOutsPerGameFromRow(row: QueryRowSet): Double
    {
        return extractPitchingStatPerGameFromRow(row, PitchingStat.OUTS)
    }

    private fun extractPitchingHitsPerGameFromRow(row: QueryRowSet): Double
    {
        return extractPitchingStatPerGameFromRow(row, PitchingStat.HITS)
    }

    private fun extractPitchingDoublesPerGameFromRow(row: QueryRowSet): Double
    {
        return extractPitchingStatPerGameFromRow(row, PitchingStat.DOUBLES)
    }

    private fun extractPitchingTriplesPerGameFromRow(row: QueryRowSet): Double
    {
        return extractPitchingStatPerGameFromRow(row, PitchingStat.TRIPLES)
    }

    private fun extractPitchingHomeRunsPerGameFromRow(row: QueryRowSet): Double
    {
        return extractPitchingStatPerGameFromRow(row, PitchingStat.HOME_RUNS)
    }

    private fun extractPitchingRunsPerGameFromRow(row: QueryRowSet): Double
    {
        return extractPitchingStatPerGameFromRow(row, PitchingStat.RUNS)
    }

    private fun extractPitchingWalksPerGameFromRow(row: QueryRowSet): Double
    {
        return extractPitchingStatPerGameFromRow(row, PitchingStat.WALKS)
    }

    private fun extractPitchingStrikeOutsPerGameFromRow(row: QueryRowSet): Double
    {
        return extractPitchingStatPerGameFromRow(row, PitchingStat.STRIKE_OUTS)
    }

    private fun extractPitchingSinglesPerGameFromRow(row: QueryRowSet): Double
    {
        val hits        = extractPitchingHitsPerGameFromRow     (row)
        val doubles     = extractPitchingDoublesPerGameFromRow  (row)
        val triples     = extractPitchingTriplesPerGameFromRow  (row)
        val homeRuns    = extractPitchingHomeRunsPerGameFromRow (row)

        val singles = hits - doubles - triples - homeRuns

        return singles
    }

    private fun extractEraPerGameFromRow(row: QueryRowSet): Double
    {
        val earnedRuns  = extractEarnedRunsPerGameFromRow   (row)
        val outs        = extractPitchingOutsPerGameFromRow (row)

        val rawEra = (earnedRuns * 9.0) / (outs / 3.0)

        val roundedEra = rawEra.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toDouble()

        return roundedEra
    }

    private fun extractWhipPerGameFromRow(row: QueryRowSet): Double
    {
        val hits    = extractPitchingHitsPerGameFromRow (row)
        val walks   = extractPitchingWalksPerGameFromRow(row)
        val outs    = extractPitchingOutsPerGameFromRow (row)

        val rawWhip = (walks + hits) / (outs / 3.0)

        val roundedWhip = rawWhip.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toDouble()

        return roundedWhip
    }

    private fun extractStrikeOutsPerNinePerGameFromRow(row: QueryRowSet): Double
    {
        val strikeOuts  = extractPitchingStrikeOutsPerGameFromRow   (row)
        val outs        = extractPitchingOutsPerGameFromRow         (row)

        val rawStrikeOutsPerNine = (strikeOuts * 9.0) / (outs / 3.0)

        val roundedStrikeOutsPerNine = rawStrikeOutsPerNine.toBigDecimal().setScale(2, RoundingMode.HALF_UP).toDouble()

        return roundedStrikeOutsPerNine
    }

    private fun extractQualityStartsPerGameFromRow(row: QueryRowSet): Double
    {
        val earnedRuns  = extractEarnedRunsPerGameFromRow   (row)
        val outs        = extractPitchingOutsPerGameFromRow (row)

        val inningsPitched = outs / 3.0

        return if (earnedRuns <= 3.0 && inningsPitched >= 6.0) 1.0 else 0.0
    }

    private fun getGame(stat: QueryRowSet): Game?
    {
        val gid         = stat[GameTable.gid        ]
        val temp        = stat[GameTable.temp       ]
        val sky         = stat[GameTable.sky        ]
        val winddir     = stat[GameTable.winddir    ]
        val windspeed   = stat[GameTable.windspeed  ]
        val precip      = stat[GameTable.precip     ]
        val dayNight    = stat[GameTable.daynight   ]
        val homeTeam    = stat[GameTable.hometeam   ]
        val visTeam     = stat[GameTable.visteam    ]

        if (gid != null && temp != null && sky != null && winddir != null && windspeed != null && precip != null
            && dayNight != null && homeTeam != null && visTeam != null) {
            val condition = Condition.valueOf(sky.uppercase())
            val precipitation = Precipitation.valueOf(precip.uppercase())
            val timeOfDay = TimeOfDay.valueOf(dayNight.uppercase())

            val windDirection = WindDirection.fromString(winddir.uppercase())

            return Game(
                gid, temp, windspeed, windDirection, condition, precipitation, timeOfDay,
                Team.valueOf(homeTeam.uppercase()), Team.valueOf(visTeam.uppercase())
            )
        }
        return null
    }
}