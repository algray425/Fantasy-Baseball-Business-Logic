package com.advanced_baseball_stats.handler.batting

import com.advanced_baseball_stats.model.batting.BattingStat
import com.advanced_baseball_stats.model.batting.HolisticBattingStatList
import com.advanced_baseball_stats.repository.PlayerBattingSql
import com.advanced_baseball_stats.repository.PlaysSql
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PerGameBattingStatHandler
{
    fun getCurrentDate(): String
    {
        val curDate             = LocalDate.now()
        val dateTimeFormatter   = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        return dateTimeFormatter.format(curDate)
    }

    fun getStats(id: String, pitcherId: String, pitcherHandedness: String, startDate: String, endDate: String, statList: List<BattingStat>): HolisticBattingStatList
    {
        val finalEndDate = if (endDate.isEmpty()) this.getCurrentDate() else endDate

        return if (pitcherId.isEmpty()) PlayerBattingSql.getBattingStatsPerGame(id, startDate, finalEndDate, statList) else PlaysSql.getBattingStatsPerGameByPitcher(id, pitcherId, pitcherHandedness, startDate, endDate, statList)
    }
}