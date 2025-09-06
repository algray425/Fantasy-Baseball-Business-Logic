package com.advanced_baseball_stats.repository

import com.advanced_baseball_stats.model.batting.BattingGame
import com.advanced_baseball_stats.model.batting.BattingGameStat
import com.advanced_baseball_stats.model.batting.BattingStat
import com.advanced_baseball_stats.model.batting.HolisticBattingStatList
import com.advanced_baseball_stats.model.common.Team
import com.advanced_baseball_stats.model.game.*
import com.advanced_baseball_stats.repository.tables.GameTable
import com.advanced_baseball_stats.repository.tables.PlayTable
import org.ktorm.dsl.*
import org.ktorm.expression.ColumnDeclaringExpression
import org.ktorm.schema.ColumnDeclaring
import java.math.RoundingMode

object PlaysSql
{
    private val battingStatToColumn: Map<BattingStat, ColumnDeclaringExpression<Int>> = mapOf(
            BattingStat.AT_BAT          to sum(PlayTable.ab     ).aliased("atBats"      )
        ,   BattingStat.SINGLE          to sum(PlayTable.single ).aliased("singles"     )
        ,   BattingStat.DOUBLE          to sum(PlayTable.double ).aliased("doubles"     )
        ,   BattingStat.TRIPLE          to sum(PlayTable.triple ).aliased("triples"     )
        ,   BattingStat.WALK            to sum(PlayTable.walk   ).aliased("walks"       )
        ,   BattingStat.HIT_BY_PITCH    to sum(PlayTable.hbp    ).aliased("hitByPitches")
        ,   BattingStat.SAC_FLY         to sum(PlayTable.sf     ).aliased("sacFlies"    )
        ,   BattingStat.HOME_RUN        to sum(PlayTable.hr     ).aliased("homeRuns"    )
    )

    private val columnToBattingStat: Map<ColumnDeclaringExpression<Int>, BattingStat> = mapOf(
            sum(PlayTable.ab        ).aliased("atBats"      )    to BattingStat.AT_BAT
        ,   sum(PlayTable.single    ).aliased("singles"     )    to BattingStat.SINGLE
        ,   sum(PlayTable.double    ).aliased("doubles"     )    to BattingStat.DOUBLE
        ,   sum(PlayTable.triple    ).aliased("triples"     )    to BattingStat.TRIPLE
        ,   sum(PlayTable.walk      ).aliased("walks"       )    to BattingStat.WALK
        ,   sum(PlayTable.hbp       ).aliased("hitByPitches")    to BattingStat.HIT_BY_PITCH
        ,   sum(PlayTable.sf        ).aliased("sacFlies"    )    to BattingStat.SAC_FLY
        ,   sum(PlayTable.hr        ).aliased("homeRuns"    )    to BattingStat.HOME_RUN
    )

    private val battingStatToColumnsNeeded: Map<BattingStat, List<ColumnDeclaring<*>>> = mapOf(
            BattingStat.AT_BAT                  to listOf(sum(PlayTable.ab).aliased("atBats"))
        ,   BattingStat.HIT                     to listOf(sum(PlayTable.single).aliased("singles"), sum(PlayTable.double).aliased("doubles"), sum(PlayTable.triple).aliased("triples"), sum(PlayTable.hr).aliased("homeRuns"))
        ,   BattingStat.BATTING_AVERAGE         to listOf(sum(PlayTable.single).aliased("singles"), sum(PlayTable.double).aliased("doubles"), sum(PlayTable.triple).aliased("triples"), sum(PlayTable.hr).aliased("homeRuns"), sum(PlayTable.ab).aliased("atBats"))
        ,   BattingStat.ON_BASE_PERCENTAGE      to listOf(sum(PlayTable.single).aliased("singles"), sum(PlayTable.double).aliased("doubles"), sum(PlayTable.triple).aliased("triples"), sum(PlayTable.hr).aliased("homeRuns"), sum(PlayTable.walk).aliased("walks"), sum(PlayTable.hbp).aliased("hitByPitches"), sum(PlayTable.sf).aliased("sacFlies"), sum(PlayTable.ab).aliased("atBats"))
        ,   BattingStat.SLUGGING_PERCENTAGE     to listOf(sum(PlayTable.single).aliased("singles"), sum(PlayTable.double).aliased("doubles"), sum(PlayTable.triple).aliased("triples"), sum(PlayTable.hr).aliased("homeRuns"), sum(PlayTable.ab).aliased("atBats"))
        ,   BattingStat.ON_BASE_PLUS_SLUGGING   to listOf(sum(PlayTable.single).aliased("singles"), sum(PlayTable.double).aliased("doubles"), sum(PlayTable.triple).aliased("triples"), sum(PlayTable.hr).aliased("homeRuns"), sum(PlayTable.walk).aliased("walks"), sum(PlayTable.hbp).aliased("hitByPitches"), sum(PlayTable.sf).aliased("sacFlies"), sum(PlayTable.ab).aliased("atBats"))
    )

    private val battingStatToPerGameBattingExtractor: Map<BattingStat, (QueryRowSet) -> Double> = mapOf(
            BattingStat.AT_BAT                  to ::extractBattingAtBatsPerGameFromRow
        ,   BattingStat.HIT                     to ::extractBattingHitsPerGameFromRow
        ,   BattingStat.BATTING_AVERAGE         to ::extractBattingAveragePerGameFromRow
        ,   BattingStat.ON_BASE_PERCENTAGE      to ::extractOnBasePercentagePerGameFromRow
        ,   BattingStat.SLUGGING_PERCENTAGE     to ::extractSluggingPercentagePerGameFromRow
        ,   BattingStat.ON_BASE_PLUS_SLUGGING   to ::extractOnBasePlusSluggingPerGameFromRow
    )

    private val gameColumns = listOf(GameTable.gid, GameTable.temp, GameTable.sky, GameTable.winddir, GameTable.windspeed, GameTable.precip,
        GameTable.daynight, GameTable.hometeam, GameTable.visteam, GameTable.site)

    fun getBattingStatsTotalByPitcher(batterId: String, pitcherId: String, pitcherHandedness: String, startDate: String, endDate: String, statList: List<BattingStat>): List<BattingGameStat>
    {
        val battingStats = mutableListOf<BattingGameStat>()

        val columnsToSelect = mutableListOf<ColumnDeclaring<*>>()

        for (stat in statList)
        {
            if (stat in battingStatToColumnsNeeded)
            {
                val columnsNeeded = battingStatToColumnsNeeded[stat]!!

                columnsToSelect.addAll(columnsNeeded)
            }
        }

        val dateRanges: ClosedRange<String> = startDate..endDate

        DatabaseConnection.database.from(PlayTable)
            .select(columnsToSelect)
            .whereWithConditions {
                it += PlayTable.batter  eq      batterId
                it += PlayTable.date    between dateRanges

                if (pitcherId.isNotEmpty())
                {
                    it += PlayTable.pitcher eq pitcherId
                }
                if (pitcherHandedness.isNotEmpty())
                {
                    it += PlayTable.pithand eq pitcherHandedness
                }
            }
            .forEach { play ->
                for (stat in statList)
                {
                    val num = battingStatToPerGameBattingExtractor[stat]?.invoke(play)!!

                    val battingGameStat = BattingGameStat(stat, num)

                    battingStats.add(battingGameStat)
                }
            }
        return battingStats
    }

    fun getBattingStatsPerGameByPitcher(batterId: String, pitcherId: String, pitcherHandedness: String, startDate: String, endDate: String, statList: List<BattingStat>): HolisticBattingStatList
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

        DatabaseConnection.database.from(PlayTable)
            .innerJoin(GameTable, on = PlayTable.gid eq GameTable.gid)
            .select(columnsToSelect)
            .whereWithConditions {
                it += PlayTable.batter  eq      batterId
                it += PlayTable.date    between dateRanges

                if (pitcherId.isNotEmpty())
                {
                    it += PlayTable.pitcher eq pitcherId
                }
                if (pitcherHandedness.isNotEmpty())
                {
                    it += PlayTable.pithand eq pitcherId
                }
            }
            .groupBy(PlayTable.gid)
            .orderBy(PlayTable.date.asc())
            .forEach { play ->
                val curGame = this.getGame(play)

                if (curGame != null)
                {
                    val battingGame = BattingGame(curGame)

                    for (stat in statList)
                    {
                        val num = battingStatToPerGameBattingExtractor[stat]?.invoke(play)!!

                        val battingGameStat = BattingGameStat(stat, num)

                        battingGame.stats.add(battingGameStat)
                    }
                    battingGames.add(battingGame)
                }
            }
        return HolisticBattingStatList(batterId, battingGames)
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

    private fun extractBattingSinglesPerGameFromRow(row: QueryRowSet): Double
    {
        return extractBattingStatPerGameFromRow(row, BattingStat.SINGLE)
    }

    private fun extractBattingDoublesPerGameFromRow(row: QueryRowSet): Double
    {
        return extractBattingStatPerGameFromRow(row, BattingStat.DOUBLE)
    }

    private fun extractBattingTriplesPerGameFromRow(row: QueryRowSet): Double
    {
        return extractBattingStatPerGameFromRow(row, BattingStat.TRIPLE)
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

    private fun extractBattingHitsPerGameFromRow(row: QueryRowSet): Double
    {
        val singles     = extractBattingSinglesPerGameFromRow   (row)
        val doubles     = extractBattingDoublesPerGameFromRow   (row)
        val triples     = extractBattingTriplesPerGameFromRow   (row)
        val homeRuns    = extractBattingHomeRunsPerGameFromRow  (row)

        return singles + doubles + triples + homeRuns
    }

    private fun extractBattingAveragePerGameFromRow(row: QueryRowSet): Double
    {
        val hits   = extractBattingHitsPerGameFromRow   (row)
        val atBats = extractBattingAtBatsPerGameFromRow (row)

        val rawBattingAverage = hits / atBats

        val roundedBattingAverage = rawBattingAverage.toBigDecimal().setScale(3, RoundingMode.HALF_UP).toDouble()

        return roundedBattingAverage
    }

    private fun extractOnBasePercentagePerGameFromRow(row: QueryRowSet): Double
    {
        val hits            = extractBattingHitsPerGameFromRow          (row)
        val walks           = extractBattingWalksPerGameFromRow         (row)
        val hitByPitches    = extractBattingHitByPitchesPerGameFromRow  (row)
        val sacFlies        = extractBattingSacFliesPerGameFromRow      (row)
        val atBats          = extractBattingAtBatsPerGameFromRow        (row)

        val rawOnBasePercentage = (hits + walks + hitByPitches) / (atBats + walks + hitByPitches + sacFlies)

        val roundedOnBasePercentage = rawOnBasePercentage.toBigDecimal().setScale(3, RoundingMode.HALF_UP).toDouble()

        return roundedOnBasePercentage
    }

    private fun extractSluggingPercentagePerGameFromRow(row: QueryRowSet): Double
    {
        val singles     = extractBattingSinglesPerGameFromRow   (row)
        val doubles     = extractBattingDoublesPerGameFromRow   (row)
        val triples     = extractBattingTriplesPerGameFromRow   (row)
        val homeRuns    = extractBattingHomeRunsPerGameFromRow  (row)
        val atBats      = extractBattingAtBatsPerGameFromRow    (row)

        val rawSluggingPercentage = (singles + doubles * 2.0 + triples * 3.0 + homeRuns * 4.0) / atBats

        val roundedSluggingPercentage = rawSluggingPercentage.toBigDecimal().setScale(3, RoundingMode.HALF_UP).toDouble()

        return roundedSluggingPercentage
    }

    private fun extractOnBasePlusSluggingPerGameFromRow(row: QueryRowSet): Double
    {
        val onBasePercentage    = extractOnBasePercentagePerGameFromRow     (row)
        val sluggingPercentage  = extractSluggingPercentagePerGameFromRow   (row)

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