package com.advanced_baseball_stats.handler.batting

import com.advanced_baseball_stats.model.batting.BattingGameStat
import com.advanced_baseball_stats.model.batting.BattingStat
import com.advanced_baseball_stats.repository.PlaysSql
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TotalBattingStatHandler
{
    fun getCurrentDate(): String
    {
        val curDate             = LocalDate.now()
        val dateTimeFormatter   = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        return dateTimeFormatter.format(curDate)
    }

    fun getStats(id: String, pitcherId: String, pitcherHandedness: String, startDate: String, endDate: String, statList: List<BattingStat>): List<BattingGameStat>
    {
        val finalEndDate = if (endDate.isEmpty()) this.getCurrentDate() else endDate

        return PlaysSql.getBattingStatsTotalByPitcher(id, pitcherId, pitcherHandedness, startDate, finalEndDate, statList)
    }
}