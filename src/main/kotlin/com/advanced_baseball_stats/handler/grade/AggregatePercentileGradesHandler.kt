package com.advanced_baseball_stats.handler.grade

import com.advanced_baseball_stats.model.batting.BattingStat
import com.advanced_baseball_stats.model.grades.HolisticBattingGrade
import com.advanced_baseball_stats.model.grades.HolisticGrade
import com.advanced_baseball_stats.model.grades.HolisticPitchingGrade
import com.advanced_baseball_stats.model.grades.PitchingGradeStat
import com.advanced_baseball_stats.repository.PlayerGradeSql

class AggregatePercentileGradesHandler : PercentileGradesHandler
{
    override fun getGrades(stat: BattingStat, percentileStart: Float, weekNumber: Int, season: Int, showAvailable: Boolean): MutableList<HolisticGrade>
    {
        return PlayerGradeSql.getAggregatePercentileGrades(stat, percentileStart, weekNumber)
    }

    override fun getGrades(stat: PitchingGradeStat, percentileStart: Float, weekNumber: Int, season: Int, showAvailable: Boolean): MutableList<HolisticGrade>
    {
        //TODO
        return mutableListOf()
    }
}