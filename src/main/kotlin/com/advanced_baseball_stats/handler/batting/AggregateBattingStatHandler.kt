package com.advanced_baseball_stats.handler.batting

import com.advanced_baseball_stats.model.batting.BattingStat
import com.advanced_baseball_stats.model.batting.HolisticBattingStatList
import com.advanced_baseball_stats.repository.PlayerBattingSql
import com.advanced_baseball_stats.utility.converter.date.DateHelper
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AggregateBattingStatHandler
{
    fun getStats(id: String, startDate: String, endDate: String, statList: List<BattingStat>): HolisticBattingStatList
    {
        val finalEndDate = if (endDate.isEmpty()) DateHelper.getCurrentDate() else endDate

        return PlayerBattingSql.getBattingStatsAggregate(id, startDate, finalEndDate, statList)
    }
}