package com.advanced_baseball_stats.handler.grade

import com.advanced_baseball_stats.model.batting.BattingStat
import com.advanced_baseball_stats.model.grades.HolisticGrade
import com.advanced_baseball_stats.model.grades.PitchingGradeStat

interface PercentileGradesHandler
{
    fun getGrades(stat: BattingStat,        percentileStart: Float, weekNumber: Int, season: Int, showAvailable: Boolean): MutableList<HolisticGrade>
    fun getGrades(stat: PitchingGradeStat,  percentileStart: Float, weekNumber: Int, season: Int, showAvailable: Boolean): MutableList<HolisticGrade>
}