package com.advanced_baseball_stats.repository

import com.advanced_baseball_stats.model.batting.BattingStat
import com.advanced_baseball_stats.model.grades.*
import com.advanced_baseball_stats.repository.tables.AggregateWeeklyGradesTable
import com.advanced_baseball_stats.repository.tables.FantasyRostersTable
import com.advanced_baseball_stats.repository.tables.WeeklyGradesTable
import org.ktorm.dsl.*
import java.math.RoundingMode

object PlayerGradeSql
{
    fun getAggregatePlayerGrades(id: String, startWeek: Int, endWeek: Int, stats: List<BattingStat>): MutableList<HolisticBattingGrade>
    {
        val grades: MutableList<HolisticBattingGrade> = mutableListOf()

        val weekNumberRanges: ClosedRange<Int>  = startWeek..endWeek

        DatabaseConnection.database.from(AggregateWeeklyGradesTable)
            .select(AggregateWeeklyGradesTable.playerId, AggregateWeeklyGradesTable.weekNumber, AggregateWeeklyGradesTable.stat, AggregateWeeklyGradesTable.aggregate_num, AggregateWeeklyGradesTable.percentile)
            .where { (AggregateWeeklyGradesTable.playerId eq id) and (AggregateWeeklyGradesTable.weekNumber between weekNumberRanges) }
//            .whereWithOrConditions {
//                for (stat in stats)
//                {
//                    it += AggregateWeeklyGradesTable.stat eq stat.toString()
//                }
//            }
            .orderBy(AggregateWeeklyGradesTable.weekNumber.asc())
            .forEach { grade ->
                val playerId    = grade[AggregateWeeklyGradesTable.playerId     ]!!
                val weekNumber  = grade[AggregateWeeklyGradesTable.weekNumber   ]!!
                val stat        = grade[AggregateWeeklyGradesTable.stat         ]!!
                val num         = grade[AggregateWeeklyGradesTable.aggregate_num]!!
                val percentile  = grade[AggregateWeeklyGradesTable.percentile   ]!!

                val convertedStat = BattingStat.valueOf(stat)

                val convertedNum            = num       .toBigDecimal().setScale(3, RoundingMode.UP).toDouble()
                val convertedPercentile     = percentile.toBigDecimal().setScale(3, RoundingMode.UP).toDouble()

                val curGrade = HolisticBattingGrade(playerId, weekNumber, convertedStat, convertedNum, convertedPercentile)

                grades.add(curGrade)
            }

        return grades
    }

    fun getPerGamePercentileBattingGrades(stat: BattingStat, percentileStart: Float, weekNumber: Int, season: Int): MutableList<HolisticGrade>
    {
        val convertedStat = stat.toString()

        return getPerGamePercentileGrades(convertedStat, percentileStart, weekNumber, season, true)
    }

    fun getPerGamePercentilePitchingGrades(stat: PitchingGradeStat, percentileStart: Float, weekNumber: Int, season: Int): MutableList<HolisticGrade>
    {
        val convertedStat = stat.toString()

        return getPerGamePercentileGrades(convertedStat, percentileStart, weekNumber, season, false)
    }

    private fun getPerGamePercentileGrades(stat: String, percentileStart: Float, weekNumber: Int, season: Int, batting: Boolean): MutableList<HolisticGrade>
    {
        val grades: MutableList<HolisticGrade> = mutableListOf()

        DatabaseConnection.database.from(WeeklyGradesTable)
            .select(WeeklyGradesTable.playerId, WeeklyGradesTable.weekNumber, WeeklyGradesTable.stat, WeeklyGradesTable.num, WeeklyGradesTable.percentile)
            .where { (WeeklyGradesTable.stat eq stat) and (WeeklyGradesTable.percentile greaterEq percentileStart) and (WeeklyGradesTable.weekNumber eq weekNumber) and (WeeklyGradesTable.season eq season) }
            .orderBy(WeeklyGradesTable.percentile.desc())
            .forEach { grade ->
                val playerId    = grade[WeeklyGradesTable.playerId      ]!!
                val weekNum     = grade[WeeklyGradesTable.weekNumber    ]!!
                val rowStat     = grade[WeeklyGradesTable.stat          ]!!
                val num         = grade[WeeklyGradesTable.num           ]!!
                val percentile  = grade[WeeklyGradesTable.percentile    ]!!

                val convertedNum            = num       .toBigDecimal().setScale(3, RoundingMode.UP).toDouble()
                val convertedPercentile     = percentile.toBigDecimal().setScale(3, RoundingMode.UP).toDouble()

                if (batting)
                {
                    val convertedStat = BattingStat.valueOf(rowStat)

                    val curGrade = HolisticBattingGrade(playerId, weekNum, convertedStat, convertedNum, convertedPercentile)

                    grades.add(curGrade)
                }
                else
                {
                    val convertedStat = PitchingGradeStat.valueOf(rowStat)

                    val curGrade = HolisticPitchingGrade(playerId, weekNum, convertedStat, convertedNum, convertedPercentile)

                    grades.add(curGrade)
                }
            }

        return grades
    }

    fun getPerGamePercentileGradesWithAvailablePlayers(stat: String, percentileStart: Float, weekNumber: Int, season: Int, isBatting: Boolean): MutableList<HolisticGrade>
    {
        val grades: MutableList<HolisticGrade> = mutableListOf()

        val rosteredPlayers = getRosteredPlayers()

        DatabaseConnection.database.from(WeeklyGradesTable)
            .select(WeeklyGradesTable.playerId, WeeklyGradesTable.stat, WeeklyGradesTable.num, WeeklyGradesTable.percentile)
            .whereWithConditions {
                it += WeeklyGradesTable.stat        eq          stat
                it += WeeklyGradesTable.percentile  greaterEq   percentileStart
                it += WeeklyGradesTable.weekNumber  eq          weekNumber
                it += WeeklyGradesTable.season      eq          season

                for (player in rosteredPlayers)
                {
                    it += WeeklyGradesTable.playerId neq player
                }
            }
            .orderBy(WeeklyGradesTable.percentile.desc())
            .forEach { grade ->
                val retroPlayerId   = grade[WeeklyGradesTable   .playerId       ]!!
                val rowStat         = grade[WeeklyGradesTable   .stat           ]!!
                val num             = grade[WeeklyGradesTable   .num            ]!!
                val percentile      = grade[WeeklyGradesTable   .percentile     ]!!

                val convertedNum            = num       .toBigDecimal().setScale(3, RoundingMode.UP).toDouble()
                val convertedPercentile     = percentile.toBigDecimal().setScale(3, RoundingMode.UP).toDouble()

                if (isBatting)
                {
                    val curGrade = HolisticBattingGrade(retroPlayerId, weekNumber, BattingStat.valueOf(rowStat), convertedNum, convertedPercentile)

                    grades.add(curGrade)
                }
                else
                {
                    val curGrade = HolisticPitchingStat(retroPlayerId, weekNumber, PitchingGradeStat.valueOf(rowStat), convertedNum, convertedPercentile)

                    grades.add(curGrade)
                }


            }

        return grades
    }

    private fun getRosteredPlayers(): MutableList<String>
    {
        val rosteredPlayers: MutableList<String> = mutableListOf()

        DatabaseConnection.database.from(FantasyRostersTable)
            .select(
                FantasyRostersTable.catcher, FantasyRostersTable.first_base, FantasyRostersTable.second_base, FantasyRostersTable.third_base,
                FantasyRostersTable.short_stop, FantasyRostersTable.second_short_stop, FantasyRostersTable.first_third_base, FantasyRostersTable.outfield_one,
                FantasyRostersTable.outfield_two, FantasyRostersTable.outfield_three, FantasyRostersTable.outfield_four, FantasyRostersTable.outfield_five,
                FantasyRostersTable.util_one, FantasyRostersTable.util_two, FantasyRostersTable.bench_one, FantasyRostersTable.bench_two,
                FantasyRostersTable.bench_three, FantasyRostersTable.bench_four, FantasyRostersTable.bench_five, FantasyRostersTable.bench_six,
                FantasyRostersTable.il_one, FantasyRostersTable.il_two, FantasyRostersTable.il_three, FantasyRostersTable.pitcher_one, FantasyRostersTable.pitcher_two,
                FantasyRostersTable.starting_pitcher_one, FantasyRostersTable.starting_pitcher_two, FantasyRostersTable.starting_pitcher_three,
                FantasyRostersTable.starting_pitcher_four, FantasyRostersTable.relief_pitcher_one, FantasyRostersTable.relief_pitcher_two)
            .forEach { team ->
                val catcher                 = team[FantasyRostersTable.catcher                  ]!!
                val firstBase               = team[FantasyRostersTable.first_base               ]!!
                val secondBase              = team[FantasyRostersTable.second_base              ]!!
                val thirdBase               = team[FantasyRostersTable.third_base               ]!!
                val shortStop               = team[FantasyRostersTable.short_stop               ]!!
                val secondShortStop         = team[FantasyRostersTable.second_short_stop        ]!!
                val firstThirdBase          = team[FantasyRostersTable.first_third_base         ]!!
                val outfieldOne             = team[FantasyRostersTable.outfield_one             ]!!
                val outfieldTwo             = team[FantasyRostersTable.outfield_two             ]!!
                val outfieldThree           = team[FantasyRostersTable.outfield_three           ]!!
                val outfieldFour            = team[FantasyRostersTable.outfield_four            ]!!
                val outfieldFive            = team[FantasyRostersTable.outfield_five            ]!!
                val utilOne                 = team[FantasyRostersTable.util_one                 ]!!
                val utilTwo                 = team[FantasyRostersTable.util_two                 ]!!
                val benchOne                = team[FantasyRostersTable.bench_one                ]!!
                val benchTwo                = team[FantasyRostersTable.bench_two                ]!!
                val benchThree              = team[FantasyRostersTable.bench_three              ]!!
                val benchFour               = team[FantasyRostersTable.bench_four               ]!!
                val benchFive               = team[FantasyRostersTable.bench_five               ]!!
                val benchSix                = team[FantasyRostersTable.bench_five               ]!!
                val ilOne                   = team[FantasyRostersTable.il_one                   ]!!
                val ilTwo                   = team[FantasyRostersTable.il_two                   ]!!
                val ilThree                 = team[FantasyRostersTable.il_three                 ]!!
                val pitcherOne              = team[FantasyRostersTable.pitcher_one              ]!!
                val pitcherTwo              = team[FantasyRostersTable.pitcher_two              ]!!
                val startingPitcherOne      = team[FantasyRostersTable.starting_pitcher_one     ]!!
                val startingPitcherTwo      = team[FantasyRostersTable.starting_pitcher_two     ]!!
                val startingPitcherThree    = team[FantasyRostersTable.starting_pitcher_three   ]!!
                val startingPitcherFour     = team[FantasyRostersTable.starting_pitcher_four    ]!!
                val reliefPitcherOne        = team[FantasyRostersTable.relief_pitcher_one       ]!!
                val reliefPitcherTwo        = team[FantasyRostersTable.relief_pitcher_two       ]!!

                if (catcher.isNotEmpty())
                {
                    rosteredPlayers.add(catcher)
                }
                if (firstBase.isNotEmpty())
                {
                    rosteredPlayers.add(firstBase)
                }
                if (secondBase.isNotEmpty())
                {
                    rosteredPlayers.add(secondBase)
                }
                if (thirdBase.isNotEmpty())
                {
                    rosteredPlayers.add(thirdBase)
                }
                if (shortStop.isNotEmpty())
                {
                    rosteredPlayers.add(shortStop)
                }
                if (secondShortStop.isNotEmpty())
                {
                    rosteredPlayers.add(secondShortStop)
                }
                if (firstThirdBase.isNotEmpty())
                {
                    rosteredPlayers.add(firstThirdBase)
                }
                if (outfieldOne.isNotEmpty())
                {
                    rosteredPlayers.add(outfieldOne)
                }
                if (outfieldTwo.isNotEmpty())
                {
                    rosteredPlayers.add(outfieldTwo)
                }
                if (outfieldThree.isNotEmpty())
                {
                    rosteredPlayers.add(outfieldThree)
                }
                if (outfieldFour.isNotEmpty())
                {
                    rosteredPlayers.add(outfieldFour)
                }
                if (outfieldFive.isNotEmpty())
                {
                    rosteredPlayers.add(outfieldFive)
                }
                if (utilOne.isNotEmpty())
                {
                    rosteredPlayers.add(utilOne)
                }
                if (utilTwo.isNotEmpty())
                {
                    rosteredPlayers.add(utilTwo)
                }
                if (benchOne.isNotEmpty())
                {
                    rosteredPlayers.add(benchOne)
                }
                if (benchTwo.isNotEmpty())
                {
                    rosteredPlayers.add(benchTwo)
                }
                if (benchThree.isNotEmpty())
                {
                    rosteredPlayers.add(benchThree)
                }
                if (benchFour.isNotEmpty())
                {
                    rosteredPlayers.add(benchFour)
                }
                if (benchFive.isNotEmpty())
                {
                    rosteredPlayers.add(benchFive)
                }
                if (benchSix.isNotEmpty())
                {
                    rosteredPlayers.add(benchSix)
                }
                if (ilOne.isNotEmpty())
                {
                    rosteredPlayers.add(ilOne)
                }
                if (ilTwo.isNotEmpty())
                {
                    rosteredPlayers.add(ilTwo)
                }
                if (ilThree.isNotEmpty())
                {
                    rosteredPlayers.add(ilThree)
                }
                if (pitcherOne.isNotEmpty())
                {
                    rosteredPlayers.add(pitcherOne)
                }
                if (pitcherTwo.isNotEmpty())
                {
                    rosteredPlayers.add(pitcherTwo)
                }
                if (startingPitcherOne.isNotEmpty())
                {
                    rosteredPlayers.add(startingPitcherOne)
                }
                if (startingPitcherTwo.isNotEmpty())
                {
                    rosteredPlayers.add(startingPitcherTwo)
                }
                if (startingPitcherThree.isNotEmpty())
                {
                    rosteredPlayers.add(startingPitcherThree)
                }
                if (startingPitcherFour.isNotEmpty())
                {
                    rosteredPlayers.add(startingPitcherFour)
                }
                if (reliefPitcherOne.isNotEmpty())
                {
                    rosteredPlayers.add(reliefPitcherOne)
                }
                if (reliefPitcherTwo.isNotEmpty())
                {
                    rosteredPlayers.add(reliefPitcherTwo)
                }
            }
        return rosteredPlayers
    }

    fun getAggregatePercentileGrades(stat: BattingStat, percentileStart: Float, weekNumber: Int): MutableList<HolisticGrade>
    {
        val grades: MutableList<HolisticGrade> = mutableListOf()

        DatabaseConnection.database.from(AggregateWeeklyGradesTable)
            .select(AggregateWeeklyGradesTable.playerId, AggregateWeeklyGradesTable.weekNumber, AggregateWeeklyGradesTable.stat, AggregateWeeklyGradesTable.aggregate_num, AggregateWeeklyGradesTable.percentile)
            .where { (AggregateWeeklyGradesTable.stat eq stat.toString()) and (AggregateWeeklyGradesTable.percentile greaterEq percentileStart) and (AggregateWeeklyGradesTable.weekNumber eq weekNumber) }
            .orderBy(AggregateWeeklyGradesTable.percentile.desc())
            .forEach { grade ->
                val playerId    = grade[AggregateWeeklyGradesTable.playerId     ]!!
                val weekNum     = grade[AggregateWeeklyGradesTable.weekNumber   ]!!
                val battingStat = grade[AggregateWeeklyGradesTable.stat         ]!!
                val num         = grade[AggregateWeeklyGradesTable.aggregate_num]!!
                val percentile  = grade[AggregateWeeklyGradesTable.percentile   ]!!

                val convertedStat = BattingStat.valueOf(battingStat)

                val convertedNum            = num       .toBigDecimal().setScale(3, RoundingMode.UP).toDouble()
                val convertedPercentile     = percentile.toBigDecimal().setScale(3, RoundingMode.UP).toDouble()

                val curGrade = HolisticBattingGrade(playerId, weekNum, convertedStat, convertedNum, convertedPercentile)

                grades.add(curGrade)
            }

        return grades
    }
}