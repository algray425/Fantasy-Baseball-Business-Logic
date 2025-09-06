package com.advanced_baseball_stats.handler.grade

import com.advanced_baseball_stats.model.batting.BattingStat
import com.advanced_baseball_stats.model.grades.HolisticBattingGrade
import com.advanced_baseball_stats.model.common.Period
import com.advanced_baseball_stats.repository.PlayerGradeSql

class AggregatePlayerGradesHandler : PlayerGradesHandler
{
    override fun getGrades(id: String, period: Period, startWeek: Int, endWeek: Int, stats: List<BattingStat>): MutableList<HolisticBattingGrade>
    {
        return PlayerGradeSql.getAggregatePlayerGrades(id, startWeek, endWeek, stats)
    }
}