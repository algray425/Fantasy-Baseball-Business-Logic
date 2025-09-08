package com.advanced_baseball_stats.repository

import com.advanced_baseball_stats.model.batting.BattingGame
import com.advanced_baseball_stats.model.batting.BattingGameStat
import com.advanced_baseball_stats.model.batting.BattingStat
import com.advanced_baseball_stats.model.batting.HolisticBattingStatList
import com.advanced_baseball_stats.model.common.Team
import com.advanced_baseball_stats.model.game.*
import com.advanced_baseball_stats.repository.tables.BatterTable
import com.advanced_baseball_stats.repository.tables.GameTable
import org.ktorm.dsl.*
import org.ktorm.schema.Column
import org.ktorm.schema.ColumnDeclaring
import java.math.RoundingMode

object PlayerBattingSql
{
    private val battingStatToColumn: Map<BattingStat, Column<Int>> = mapOf(
            BattingStat.AT_BAT          to BatterTable.b_ab
        ,   BattingStat.HIT             to BatterTable.b_h
        ,   BattingStat.RUN             to BatterTable.b_r
        ,   BattingStat.RBI             to BatterTable.b_rbi
        ,   BattingStat.DOUBLE          to BatterTable.b_d
        ,   BattingStat.TRIPLE          to BatterTable.b_t
        ,   BattingStat.HOME_RUN        to BatterTable.b_hr
        ,   BattingStat.WALK            to BatterTable.b_w
        ,   BattingStat.HIT_BY_PITCH    to BatterTable.b_hbp
        ,   BattingStat.SAC_FLY         to BatterTable.b_sf
        ,   BattingStat.STRIKEOUT       to BatterTable.b_k
        ,   BattingStat.STOLEN_BASE     to BatterTable.b_sb
    )

    private val columnToBattingStat: Map<Column<Int>, BattingStat> = mapOf(
            BatterTable.b_ab    to BattingStat.AT_BAT
        ,   BatterTable.b_h     to BattingStat.HIT
        ,   BatterTable.b_r     to BattingStat.RUN
        ,   BatterTable.b_rbi   to BattingStat.RBI
        ,   BatterTable.b_d     to BattingStat.DOUBLE
        ,   BatterTable.b_t     to BattingStat.TRIPLE
        ,   BatterTable.b_hr    to BattingStat.HOME_RUN
        ,   BatterTable.b_w     to BattingStat.WALK
        ,   BatterTable.b_hbp   to BattingStat.HIT_BY_PITCH
        ,   BatterTable.b_sf    to BattingStat.SAC_FLY
        ,   BatterTable.b_k     to BattingStat.STRIKEOUT
        ,   BatterTable.b_sb    to BattingStat.STOLEN_BASE
    )

    private val battingStatToColumnsNeeded: Map<BattingStat, List<ColumnDeclaring<*>>> = mapOf(
            BattingStat.AT_BAT                  to listOf(BatterTable.b_ab  )
        ,   BattingStat.HIT                     to listOf(BatterTable.b_h   )
        ,   BattingStat.RUN                     to listOf(BatterTable.b_r   )
        ,   BattingStat.RBI                     to listOf(BatterTable.b_rbi )
        ,   BattingStat.SINGLE                  to listOf(BatterTable.b_h, BatterTable.b_d, BatterTable.b_t, BatterTable.b_hr)
        ,   BattingStat.DOUBLE                  to listOf(BatterTable.b_d   )
        ,   BattingStat.TRIPLE                  to listOf(BatterTable.b_t   )
        ,   BattingStat.HOME_RUN                to listOf(BatterTable.b_hr  )
        ,   BattingStat.WALK                    to listOf(BatterTable.b_w   )
        ,   BattingStat.HIT_BY_PITCH            to listOf(BatterTable.b_hbp )
        ,   BattingStat.SAC_FLY                 to listOf(BatterTable.b_sf  )
        ,   BattingStat.STRIKEOUT               to listOf(BatterTable.b_k   )
        ,   BattingStat.STOLEN_BASE             to listOf(BatterTable.b_sb  )
        ,   BattingStat.BATTING_AVERAGE         to listOf(BatterTable.b_h, BatterTable.b_ab)
        ,   BattingStat.SLUGGING_PERCENTAGE     to listOf(BatterTable.b_h, BatterTable.b_d, BatterTable.b_t, BatterTable.b_hr, BatterTable.b_ab)
        ,   BattingStat.ON_BASE_PERCENTAGE      to listOf(BatterTable.b_h, BatterTable.b_w, BatterTable.b_hbp, BatterTable.b_sf, BatterTable.b_ab)
        ,   BattingStat.ON_BASE_PLUS_SLUGGING   to listOf(BatterTable.b_h, BatterTable.b_d, BatterTable.b_t, BatterTable.b_hr, BatterTable.b_w, BatterTable.b_hbp, BatterTable.b_sf, BatterTable.b_ab)
    )

    private val battingStatToPerGameBattingExtractor: Map<BattingStat, (QueryRowSet)->Double> = mapOf(
            BattingStat.AT_BAT                      to ::extractBattingAtBatsPerGameFromRow
        ,   BattingStat.HIT                         to ::extractBattingHitsPerGameFromRow
        ,   BattingStat.RUN                         to ::extractBattingRunsPerGameFromRow
        ,   BattingStat.RBI                         to ::extractBattingRbisPerGameFromRow
        ,   BattingStat.SINGLE                      to ::extractBattingSinglesPerGameFromRow
        ,   BattingStat.DOUBLE                      to ::extractBattingDoublesPerGameFromRow
        ,   BattingStat.TRIPLE                      to ::extractBattingTriplesPerGameFromRow
        ,   BattingStat.HOME_RUN                    to ::extractBattingHomeRunsPerGameFromRow
        ,   BattingStat.WALK                        to ::extractBattingWalksPerGameFromRow
        ,   BattingStat.HIT_BY_PITCH                to ::extractBattingHitByPitchesPerGameFromRow
        ,   BattingStat.SAC_FLY                     to ::extractBattingSacFliesPerGameFromRow
        ,   BattingStat.STRIKEOUT                   to ::extractBattingStrikeoutsPerGameFromRow
        ,   BattingStat.STOLEN_BASE                 to ::extractBattingStolenBasesPerGameFromRow
        ,   BattingStat.BATTING_AVERAGE             to ::extractBattingAveragePerGameFromRow
        ,   BattingStat.SLUGGING_PERCENTAGE         to ::extractSluggingPercentagePerGameFromRow
        ,   BattingStat.ON_BASE_PERCENTAGE          to ::extractOnBasePercentagePerGameFromRow
        ,   BattingStat.ON_BASE_PLUS_SLUGGING       to ::extractOnBasePlusSluggingPerGameFromRow
    )

    private val battingStatToAggregateBattingExtractor: Map<BattingStat, (QueryRowSet, MutableMap<BattingStat, Double>, MutableMap<BattingStat, Boolean>)->Double> = mapOf(
            BattingStat.AT_BAT                  to ::extractBattingAtBatsAggregateFromRow
        ,   BattingStat.HIT                     to ::extractBattingHitsAggregateFromRow
        ,   BattingStat.RUN                     to ::extractBattingRunsAggregateFromRow
        ,   BattingStat.RBI                     to ::extractBattingRbisAggregateFromRow
        ,   BattingStat.SINGLE                  to ::extractBattingSinglesAggregateFromRow
        ,   BattingStat.DOUBLE                  to ::extractBattingDoublesAggregateFromRow
        ,   BattingStat.TRIPLE                  to ::extractBattingTriplesAggregateFromRow
        ,   BattingStat.HOME_RUN                to ::extractBattingHomeRunsAggregateFromRow
        ,   BattingStat.WALK                    to ::extractBattingWalksAggregateFromRow
        ,   BattingStat.HIT_BY_PITCH            to ::extractBattingHitByPitchesAggregateFromRow
        ,   BattingStat.SAC_FLY                 to ::extractBattingSacFliesAggregateFromRow
        ,   BattingStat.STRIKEOUT               to ::extractBattingStrikeoutsAggregateFromRow
        ,   BattingStat.STOLEN_BASE             to ::extractBattingStolenBasesAggregateFromRow
        ,   BattingStat.BATTING_AVERAGE         to ::extractBattingAverageAggregateFromRow
        ,   BattingStat.SLUGGING_PERCENTAGE     to ::extractSluggingPercentageAggregateFromRow
        ,   BattingStat.ON_BASE_PERCENTAGE      to ::extractOnBasePercentageAggregateFromRow
        ,   BattingStat.ON_BASE_PLUS_SLUGGING   to ::extractOnBasePlusSluggingAggregateFromRow
    )

    private val gameColumns = listOf(GameTable.gid, GameTable.temp, GameTable.sky, GameTable.winddir, GameTable.windspeed, GameTable.precip,
        GameTable.daynight, GameTable.hometeam, GameTable.visteam, GameTable.site)

    fun getBattingStatsAggregate(id: String, startDate: String, endDate: String, statList: List<BattingStat>): HolisticBattingStatList
    {
        val battingGames = mutableListOf<BattingGame>()

        val battingStatToSum = mutableMapOf<BattingStat, Double>()
        val columnsToSelect  = mutableListOf<ColumnDeclaring<*>>()

        for (stat in statList)
        {
            if (stat in battingStatToColumnsNeeded)
            {
                val columnsNeeded = battingStatToColumnsNeeded[stat]!!

                for (column in columnsNeeded)
                {
                    val curStat = columnToBattingStat[column]!!

                    battingStatToSum[curStat] = 0.0
                }

                columnsToSelect.addAll(columnsNeeded)
            }
        }

        columnsToSelect.addAll(gameColumns)

        val dateRanges: ClosedRange<String> = startDate..endDate

        DatabaseConnection.database.from(BatterTable)
            .innerJoin(GameTable, on = BatterTable.gid eq GameTable.gid)
            .select(columnsToSelect)
            .where { (BatterTable.id eq id) and (BatterTable.date between dateRanges) }
            .orderBy(BatterTable.date.asc())
            .forEach { batterRow ->
                val curGame = this.getGame(batterRow)

                if (curGame != null)
                {
                    val updated = mutableMapOf<BattingStat, Boolean>()

                    val battingGame = BattingGame(curGame)

                    for (stat in statList)
                    {
                        val num = battingStatToAggregateBattingExtractor[stat]?.invoke(batterRow, battingStatToSum, updated)!!

                        val battingGameStat = BattingGameStat(stat, num)

                        battingGame.stats.add(battingGameStat)
                    }
                    battingGames.add(battingGame)
                }
            }
        return HolisticBattingStatList(id, battingGames)
    }

    private fun extractBattingStatAggregateFromRow(row: QueryRowSet, battingStat: BattingStat, battingStatToSum: MutableMap<BattingStat, Double>, updated: MutableMap<BattingStat, Boolean>): Double
    {
        if (!updated.containsKey(battingStat))
        {
            val previousNum    = battingStatToSum[battingStat]!!

            val columnToExtract = battingStatToColumn[battingStat]!!

            val curNum         = row[columnToExtract]!!

            val totalNum = previousNum + curNum

            battingStatToSum[battingStat] = totalNum
            updated         [battingStat] = true

            return totalNum
        }
        else
        {
            val num = battingStatToSum[battingStat]!!

            return num
        }
    }

    private fun extractBattingAtBatsAggregateFromRow(row: QueryRowSet, battingStatToSum: MutableMap<BattingStat, Double>, updated: MutableMap<BattingStat, Boolean>): Double
    {
        return extractBattingStatAggregateFromRow(row, BattingStat.AT_BAT, battingStatToSum, updated)
    }

    private fun extractBattingHitsAggregateFromRow(row: QueryRowSet, battingStatToSum: MutableMap<BattingStat, Double>, updated: MutableMap<BattingStat, Boolean>): Double
    {
        return extractBattingStatAggregateFromRow(row, BattingStat.HIT, battingStatToSum, updated)
    }

    private fun extractBattingRunsAggregateFromRow(row: QueryRowSet, battingStatToSum: MutableMap<BattingStat, Double>, updated: MutableMap<BattingStat, Boolean>): Double
    {
        return extractBattingStatAggregateFromRow(row, BattingStat.RUN, battingStatToSum, updated)
    }

    private fun extractBattingRbisAggregateFromRow(row: QueryRowSet, battingStatToSum: MutableMap<BattingStat, Double>, updated: MutableMap<BattingStat, Boolean>): Double
    {
        return extractBattingStatAggregateFromRow(row, BattingStat.RBI, battingStatToSum, updated)
    }

    private fun extractBattingDoublesAggregateFromRow(row: QueryRowSet, battingStatToSum: MutableMap<BattingStat, Double>, updated: MutableMap<BattingStat, Boolean>): Double
    {
        return extractBattingStatAggregateFromRow(row, BattingStat.DOUBLE, battingStatToSum, updated)
    }

    private fun extractBattingTriplesAggregateFromRow(row: QueryRowSet, battingStatToSum: MutableMap<BattingStat, Double>, updated: MutableMap<BattingStat, Boolean>): Double
    {
        return extractBattingStatAggregateFromRow(row, BattingStat.TRIPLE, battingStatToSum, updated)
    }

    private fun extractBattingHomeRunsAggregateFromRow(row: QueryRowSet, battingStatToSum: MutableMap<BattingStat, Double>, updated: MutableMap<BattingStat, Boolean>): Double
    {
        return extractBattingStatAggregateFromRow(row, BattingStat.HOME_RUN, battingStatToSum, updated)
    }

    private fun extractBattingWalksAggregateFromRow(row: QueryRowSet, battingStatToSum: MutableMap<BattingStat, Double>, updated: MutableMap<BattingStat, Boolean>): Double
    {
        return extractBattingStatAggregateFromRow(row, BattingStat.WALK, battingStatToSum, updated)
    }

    private fun extractBattingHitByPitchesAggregateFromRow(row: QueryRowSet, battingStatToSum: MutableMap<BattingStat, Double>, updated: MutableMap<BattingStat, Boolean>): Double
    {
        return extractBattingStatAggregateFromRow(row, BattingStat.HIT_BY_PITCH, battingStatToSum, updated)
    }

    private fun extractBattingSacFliesAggregateFromRow(row: QueryRowSet, battingStatToSum: MutableMap<BattingStat, Double>, updated: MutableMap<BattingStat, Boolean>): Double
    {
        return extractBattingStatAggregateFromRow(row, BattingStat.SAC_FLY, battingStatToSum, updated)
    }

    private fun extractBattingStrikeoutsAggregateFromRow(row: QueryRowSet, battingStatToSum: MutableMap<BattingStat, Double>, updated: MutableMap<BattingStat, Boolean>): Double
    {
        return extractBattingStatAggregateFromRow(row, BattingStat.STRIKEOUT, battingStatToSum, updated)
    }

    private fun extractBattingStolenBasesAggregateFromRow(row: QueryRowSet, battingStatToSum: MutableMap<BattingStat, Double>, updated: MutableMap<BattingStat, Boolean>): Double
    {
        return extractBattingStatAggregateFromRow(row, BattingStat.STOLEN_BASE, battingStatToSum, updated)
    }

    private fun extractBattingSinglesAggregateFromRow(row: QueryRowSet, battingStatToSum: MutableMap<BattingStat, Double>, updated: MutableMap<BattingStat, Boolean>): Double
    {
        val hits        = extractBattingStatAggregateFromRow(row, BattingStat.HIT       , battingStatToSum, updated)
        val doubles     = extractBattingStatAggregateFromRow(row, BattingStat.DOUBLE    , battingStatToSum, updated)
        val triples     = extractBattingStatAggregateFromRow(row, BattingStat.TRIPLE    , battingStatToSum, updated)
        val homeRuns    = extractBattingStatAggregateFromRow(row, BattingStat.HOME_RUN  , battingStatToSum, updated)

        val singles = hits - doubles - triples - homeRuns

        return singles
    }

    private fun extractBattingAverageAggregateFromRow(row: QueryRowSet, battingStatToSum: MutableMap<BattingStat, Double>, updated: MutableMap<BattingStat, Boolean>): Double
    {
        val atBats  = extractBattingStatAggregateFromRow(row, BattingStat.AT_BAT,   battingStatToSum, updated)
        val hits    = extractBattingStatAggregateFromRow(row, BattingStat.HIT,      battingStatToSum, updated)

        val rawBattingAverage = hits / atBats

        val roundedBattingAverage = rawBattingAverage.toBigDecimal().setScale(3, RoundingMode.HALF_UP).toDouble()

        return roundedBattingAverage
    }

    private fun extractSluggingPercentageAggregateFromRow(row: QueryRowSet, battingStatToSum: MutableMap<BattingStat, Double>, updated: MutableMap<BattingStat, Boolean>): Double
    {
        val hits        = extractBattingStatAggregateFromRow(row, BattingStat.HIT,      battingStatToSum, updated)
        val doubles     = extractBattingStatAggregateFromRow(row, BattingStat.DOUBLE,   battingStatToSum, updated)
        val triples     = extractBattingStatAggregateFromRow(row, BattingStat.TRIPLE,   battingStatToSum, updated)
        val homeRuns    = extractBattingStatAggregateFromRow(row, BattingStat.HOME_RUN, battingStatToSum, updated)
        val atBats      = extractBattingStatAggregateFromRow(row, BattingStat.AT_BAT,   battingStatToSum, updated)

        val singles = hits - doubles - triples - homeRuns

        val rawSluggingPercentage = (singles + doubles * 2.0 + triples * 3.0 + homeRuns * 4.0) / atBats

        val roundedSluggingPercentage = rawSluggingPercentage.toBigDecimal().setScale(3, RoundingMode.HALF_UP).toDouble()

        return roundedSluggingPercentage
    }

    private fun extractOnBasePercentageAggregateFromRow(row: QueryRowSet, battingStatToSum: MutableMap<BattingStat, Double>, updated: MutableMap<BattingStat, Boolean>): Double
    {
        val hits            = extractBattingStatAggregateFromRow(row, BattingStat.HIT,             battingStatToSum, updated)
        val walks           = extractBattingStatAggregateFromRow(row, BattingStat.WALK,            battingStatToSum, updated)
        val hitByPitches    = extractBattingStatAggregateFromRow(row, BattingStat.HIT_BY_PITCH,    battingStatToSum, updated)
        val sacFlies        = extractBattingStatAggregateFromRow(row, BattingStat.SAC_FLY,         battingStatToSum, updated)
        val atBats          = extractBattingStatAggregateFromRow(row, BattingStat.AT_BAT,          battingStatToSum, updated)

        val rawOnBasePercentage = (hits + walks + hitByPitches) / (atBats + walks + hitByPitches + sacFlies)

        val roundedOnBasePercentage = rawOnBasePercentage.toBigDecimal().setScale(3, RoundingMode.HALF_UP).toDouble()

        return roundedOnBasePercentage
    }

    private fun extractOnBasePlusSluggingAggregateFromRow(row: QueryRowSet, battingStatToSum: MutableMap<BattingStat, Double>, updated: MutableMap<BattingStat, Boolean>): Double
    {
        val onBasePercentage    = extractOnBasePercentageAggregateFromRow   (row, battingStatToSum, updated)
        val sluggingPercentage  = extractSluggingPercentageAggregateFromRow (row, battingStatToSum, updated)

        val onBasePlusSlugging = onBasePercentage + sluggingPercentage

        return onBasePlusSlugging
    }

    fun getBattingStatsPerGame(id: String, startDate: String, endDate: String, statList: List<BattingStat>): HolisticBattingStatList
    {
        val battingGames = mutableListOf<BattingGame>()

        val columnsToSelect = mutableListOf<ColumnDeclaring<*>>()

        for (stat in statList)
        {
            if (stat in battingStatToColumnsNeeded)
            {
                val columnsNeeded = battingStatToColumnsNeeded[stat]!!

                columnsToSelect.addAll(columnsNeeded)
            }
        }

        columnsToSelect.addAll(gameColumns)

        val dateRanges: ClosedRange<String> = startDate..endDate

        DatabaseConnection.database.from(BatterTable)
            .innerJoin(GameTable, on = BatterTable.gid eq GameTable.gid)
            .select(columnsToSelect)
            .where { (BatterTable.id eq id) and (BatterTable.date between dateRanges) }
            .orderBy(BatterTable.date.asc())
            .forEach { batterRow ->
                val curGame = this.getGame(batterRow)

                if (curGame != null)
                {
                    val battingGame = BattingGame(curGame)

                    for (stat in statList)
                    {
                        val num = battingStatToPerGameBattingExtractor[stat]?.invoke(batterRow)!!

                        val battingGameStat = BattingGameStat(stat, num)

                        battingGame.stats.add(battingGameStat)
                    }
                    battingGames.add(battingGame)
                }
            }
        return HolisticBattingStatList(id, battingGames)
    }

    private fun extractBattingStatPerGameFromRow(row: QueryRowSet, stat: BattingStat): Double
    {
        val column = battingStatToColumn[stat]!!

        val num = row[column]!!

        return num.toDouble()
    }

    private fun extractBattingAtBatsPerGameFromRow(row: QueryRowSet): Double
    {
        return extractBattingStatPerGameFromRow(row, BattingStat.AT_BAT)
    }

    private fun extractBattingHitsPerGameFromRow(row: QueryRowSet): Double
    {
        return extractBattingStatPerGameFromRow(row, BattingStat.HIT)
    }

    private fun extractBattingRunsPerGameFromRow(row: QueryRowSet): Double
    {
        return extractBattingStatPerGameFromRow(row, BattingStat.RUN)
    }

    private fun extractBattingStrikeoutsPerGameFromRow(row: QueryRowSet): Double
    {
        return extractBattingStatPerGameFromRow(row, BattingStat.STRIKEOUT)
    }

    private fun extractBattingStolenBasesPerGameFromRow(row: QueryRowSet): Double
    {
        return extractBattingStatPerGameFromRow(row, BattingStat.STOLEN_BASE)
    }

    private fun extractBattingDoublesPerGameFromRow(row: QueryRowSet): Double
    {
        return extractBattingStatPerGameFromRow(row, BattingStat.DOUBLE)
    }

    private fun extractBattingTriplesPerGameFromRow(row: QueryRowSet): Double
    {
        return extractBattingStatPerGameFromRow(row, BattingStat.TRIPLE)
    }

    private fun extractBattingRbisPerGameFromRow(row: QueryRowSet): Double
    {
        return extractBattingStatPerGameFromRow(row, BattingStat.RBI)
    }

    private fun extractBattingHomeRunsPerGameFromRow(row: QueryRowSet): Double
    {
        return extractBattingStatPerGameFromRow(row, BattingStat.HOME_RUN)
    }

    private fun extractBattingWalksPerGameFromRow(row: QueryRowSet): Double
    {
        return extractBattingStatPerGameFromRow(row, BattingStat.WALK)
    }

    private fun extractBattingHitByPitchesPerGameFromRow(row: QueryRowSet): Double
    {
        return extractBattingStatPerGameFromRow(row, BattingStat.HIT_BY_PITCH)
    }

    private fun extractBattingSacFliesPerGameFromRow(row: QueryRowSet): Double
    {
        return extractBattingStatPerGameFromRow(row, BattingStat.SAC_FLY)
    }

    private fun extractBattingSinglesPerGameFromRow(row: QueryRowSet): Double
    {
        val hits        = extractBattingHitsPerGameFromRow      (row)
        val doubles     = extractBattingDoublesPerGameFromRow   (row)
        val triples     = extractBattingTriplesPerGameFromRow   (row)
        val homeRuns    = extractBattingHomeRunsPerGameFromRow  (row)

        val singles = hits - doubles - triples - homeRuns

        return singles
    }

    private fun extractBattingAveragePerGameFromRow(row: QueryRowSet): Double
    {
        val atBats  = extractBattingAtBatsPerGameFromRow   (row)
        val hits    = extractBattingHitsPerGameFromRow     (row)

        val rawBattingAverage = hits / atBats

        val roundedBattingAverage = rawBattingAverage.toBigDecimal().setScale(3, RoundingMode.HALF_UP).toDouble()

        return roundedBattingAverage
    }

    private fun extractSluggingPercentagePerGameFromRow(row: QueryRowSet): Double
    {
        val atBats      = extractBattingAtBatsPerGameFromRow   (row)
        val hits        = extractBattingHitsPerGameFromRow     (row)
        val doubles     = extractBattingDoublesPerGameFromRow  (row)
        val triples     = extractBattingTriplesPerGameFromRow  (row)
        val homeRuns    = extractBattingHomeRunsPerGameFromRow (row)

        if (atBats <= 0.0)
        {
            return 0.0
        }

        val singles = hits - doubles - triples - homeRuns

        val rawSluggingPercentage = (singles + doubles * 2.0 + triples * 3.0 + homeRuns * 4.0) / atBats

        val roundedSluggingPercentage = rawSluggingPercentage.toBigDecimal().setScale(3, RoundingMode.HALF_UP).toDouble()

        return roundedSluggingPercentage
    }

    private fun extractOnBasePercentagePerGameFromRow(row: QueryRowSet): Double
    {
        val atBats          = extractBattingAtBatsPerGameFromRow       (row)
        val hits            = extractBattingHitsPerGameFromRow         (row)
        val walks           = extractBattingWalksPerGameFromRow        (row)
        val hitByPitches    = extractBattingHitByPitchesPerGameFromRow (row)
        val sacFlies        = extractBattingSacFliesPerGameFromRow     (row)

        if (atBats <= 0.0 && walks <= 0.0 && sacFlies <= 0.0)
        {
            return 0.0
        }

        val rawOnBasePercentage = (hits + walks + hitByPitches) / (atBats + walks + hitByPitches + sacFlies)

        val roundedOnBasePercentage = rawOnBasePercentage.toBigDecimal().setScale(3, RoundingMode.HALF_UP).toDouble()

        return roundedOnBasePercentage
    }

    private fun extractOnBasePlusSluggingPerGameFromRow(row: QueryRowSet): Double
    {
        val onBasePercentage    = extractOnBasePercentagePerGameFromRow    (row)
        val sluggingPercentage  = extractSluggingPercentagePerGameFromRow  (row)

        val onBasePlusSlugging = onBasePercentage + sluggingPercentage

        return onBasePlusSlugging
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