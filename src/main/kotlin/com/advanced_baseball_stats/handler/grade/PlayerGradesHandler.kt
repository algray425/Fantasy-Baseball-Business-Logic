package com.advanced_baseball_stats.handler.grade

import com.advanced_baseball_stats.model.batting.BattingStat
import com.advanced_baseball_stats.model.grades.HolisticBattingGrade
import com.advanced_baseball_stats.model.common.Period

interface PlayerGradesHandler
{
    fun getGrades(id: String, period: Period, startWeek: Int, endWeek: Int, stats: List<BattingStat>): MutableList<HolisticBattingGrade>
}