package com.advanced_baseball_stats.handler.batting

import com.advanced_baseball_stats.model.batting.BattingGameStat
import com.advanced_baseball_stats.model.batting.BattingStat
import com.advanced_baseball_stats.repository.PlaysSql
import com.advanced_baseball_stats.utility.converter.date.DateHelper

class TotalBattingStatHandler
{
    fun getStats(id: String, pitcherId: String, pitcherHandedness: String, startDate: String, endDate: String, statList: List<BattingStat>): List<BattingGameStat>
    {
        val finalEndDate = if (endDate.isEmpty()) DateHelper.getCurrentDate() else endDate

        //TODO: Add equivalent function in playerbattingsql and swap based on if pitcher info is provided
        return PlaysSql.getBattingStatsTotalByPitcher(id, pitcherId, pitcherHandedness, startDate, finalEndDate, statList)
    }
}