package com.advanced_baseball_stats.handler.grade

import com.advanced_baseball_stats.model.batting.BattingStat
import com.advanced_baseball_stats.model.grades.HolisticBattingGrade
import com.advanced_baseball_stats.model.common.Period
import com.advanced_baseball_stats.model.grades.HolisticGrade
import com.advanced_baseball_stats.model.grades.PitchingGradeStat
import com.advanced_baseball_stats.utility.converter.PeriodConverter

object GradeHandler
{
    private val periodToPlayerGradeHandler: Map<Period, PlayerGradesHandler> = mapOf(
        Period.AGGREGATE to AggregatePlayerGradesHandler()
    )

    private val periodToPercentileGradeHandler: Map<Period, PercentileGradesHandler> = mapOf(
        Period.AGGREGATE    to AggregatePercentileGradesHandler (),
        Period.PER_GAME     to PerGamePercentileGradesHandler   ()
    )

    fun getGradesByPlayer(id: String, period: String, startWeekNumber: String, endWeekNumber: String, stats: List<String>): MutableList<HolisticBattingGrade>
    {
        val convertedPeriod = PeriodConverter.convertPeriod(period)

        val convertedStartWeekNumber = startWeekNumber.toInt()

        //TODO: Do all MLB seasons have the same number of weeks?... might be good to cross reference this with a year parameter
        val convertedEndWeekNumber = if (endWeekNumber.isEmpty()) 29 else endWeekNumber .toInt()

        val battingStats: MutableList<BattingStat> = mutableListOf()

        for (stat in stats)
        {
            if (stat.isEmpty())
            {
                battingStats.add(BattingStat.ON_BASE_PERCENTAGE)
                battingStats.add(BattingStat.RUN)
                battingStats.add(BattingStat.HOME_RUN)
                battingStats.add(BattingStat.RBI)
                battingStats.add(BattingStat.STOLEN_BASE)
                battingStats.add(BattingStat.TOTAL)
            }
            else
            {
                val curStat = BattingStat.valueOf(stat.uppercase())
                battingStats.add(curStat)
            }
        }

        if (stats.isEmpty())
        {
            return mutableListOf()
        }

        val handler = periodToPlayerGradeHandler[convertedPeriod]

        return handler?.getGrades(id, convertedPeriod, convertedStartWeekNumber, convertedEndWeekNumber, battingStats) ?: mutableListOf()
    }

    fun getBattingGradesByPercentile(stat: String, period: String, percentileStart: String, weekNumber: String, season: String, showAvailable: String): MutableList<HolisticGrade>
    {
        val battingStat     = BattingStat.valueOf(stat.uppercase())
        val convertedPeriod = PeriodConverter.convertPeriod(period)

        val convertedPercentileStart    = percentileStart   .toFloat    ()
        val convertedWeekNumber         = weekNumber        .toInt      ()
        val convertedSeason             = season            .toInt      ()
        val convertedShowAvailable      = showAvailable     .toBoolean  ()

        val handler = periodToPercentileGradeHandler[convertedPeriod]

        return handler?.getGrades(battingStat, convertedPercentileStart, convertedWeekNumber, convertedSeason, convertedShowAvailable) ?: mutableListOf()
    }

    fun getPitchingGradesByPercentile(stat: String, period: String, percentileStart: String, weekNumber: String, season: String, showAvailable: String): MutableList<HolisticGrade>
    {
        val pitchingStat    = PitchingGradeStat.valueOf(stat.uppercase())
        val convertedPeriod = PeriodConverter.convertPeriod(period)

        val convertedPercentileStart    = percentileStart   .toFloat    ()
        val convertedWeekNumber         = weekNumber        .toInt      ()
        val convertedSeason             = season            .toInt      ()
        val convertedShowAvailable      = showAvailable     .toBoolean  ()

        val handler = periodToPercentileGradeHandler[convertedPeriod]

        return handler?.getGrades(pitchingStat, convertedPercentileStart, convertedWeekNumber, convertedSeason, convertedShowAvailable) ?: mutableListOf()
    }
}