package com.advanced_baseball_stats.handler.batting

import com.advanced_baseball_stats.exception.UnknownBattingStatException
import com.advanced_baseball_stats.model.batting.BattingStat
import com.advanced_baseball_stats.model.common.Period
import com.advanced_baseball_stats.utility.converter.PeriodConverter

object BattingStatHandler
{
    private val perGameBattingStatHandler   = PerGameBattingStatHandler     ()
    private val aggregateBattingStatHandler = AggregateBattingStatHandler   ()
    private val totalBattingStatHandler     = TotalBattingStatHandler       ()

    fun getBattingStatV2(id: String, pitcherId: String, pitcherHandedness: String, period: String, startDate: String, endDate: String, statList: List<String>): Any
    {
        val battingStatList = mutableListOf<BattingStat>()

        for (stat in statList)
        {
            try
            {
                val battingStat = BattingStat.valueOf(stat)

                battingStatList.add(battingStat)
            }
            catch (ex: IllegalArgumentException)
            {
                throw UnknownBattingStatException("$stat is not a valid batting stat!")
            }
        }

        val convertedPeriod = PeriodConverter.convertPeriod(period)

        if (Period.PER_GAME.equals(convertedPeriod))
        {
            return perGameBattingStatHandler.getStats(id, pitcherId, pitcherHandedness, startDate, endDate, battingStatList)
        }
        else if (Period.AGGREGATE.equals(convertedPeriod))
        {
            return aggregateBattingStatHandler.getStats(id, startDate, endDate, battingStatList)
        }
        else
        {
            return totalBattingStatHandler.getStats(id, pitcherId, pitcherHandedness, startDate, endDate, battingStatList)
        }
    }
}