package com.advanced_baseball_stats.handler.batting

import com.advanced_baseball_stats.model.batting.BattingStat
import com.advanced_baseball_stats.model.batting.HolisticBattingStatList
import com.advanced_baseball_stats.repository.PlayerBattingSql
import com.advanced_baseball_stats.repository.PlaysSql
import com.advanced_baseball_stats.utility.converter.date.DateHelper

class PerGameBattingStatHandler
{
    fun getStats(id: String, pitcherId: String, pitcherHandedness: String, startDate: String, endDate: String, statList: List<BattingStat>): HolisticBattingStatList
    {
        val finalEndDate = if (endDate.isEmpty()) DateHelper.getCurrentDate() else endDate

        return if (pitcherId.isEmpty() && pitcherHandedness.isEmpty()) PlayerBattingSql.getBattingStatsPerGame(id, startDate, finalEndDate, statList) else PlaysSql.getBattingStatsPerGameByPitcher(id, pitcherId, pitcherHandedness, startDate, finalEndDate, statList)
    }
}