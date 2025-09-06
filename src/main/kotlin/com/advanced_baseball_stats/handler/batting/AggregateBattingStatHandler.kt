package com.advanced_baseball_stats.handler.batting

import com.advanced_baseball_stats.model.batting.BattingStat
import com.advanced_baseball_stats.model.batting.HolisticBattingStatList
import com.advanced_baseball_stats.repository.PlayerBattingSql
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AggregateBattingStatHandler
{
    fun getCurrentDate(): String
    {
        val curDate             = LocalDate.now()
        val dateTimeFormatter   = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        return dateTimeFormatter.format(curDate)
    }

    fun getStats(id: String, startDate: String, endDate: String, statList: List<BattingStat>): HolisticBattingStatList
    {
        val finalEndDate = if (endDate.isEmpty()) this.getCurrentDate() else endDate

        return PlayerBattingSql.getBattingStatsAggregate(id, startDate, finalEndDate, statList)
    }
}