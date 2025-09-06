package com.advanced_baseball_stats.handler.pitching

import com.advanced_baseball_stats.model.common.Period
import com.advanced_baseball_stats.model.pitching.PitchingStat

object PitchingStatHandler
{
    private val aggregatePitchingStatHandler    = AggregatePitchingStatHandler  ()
    private val perGamePitchingStatHandler      = PerGamePitchingStatHandler    ()

    fun getPitchingStatV2(id: String, batterId: String, batterSide: String, period: String, startDate: String, endDate: String, statList: List<String>): Any
    {
        val pitchingStatList = mutableListOf<PitchingStat>()

        for (stat in statList)
        {
            pitchingStatList.add(PitchingStat.valueOf(stat))
        }

        val convertedPeriod = Period.valueOf(period)

        if (Period.AGGREGATE.equals(convertedPeriod))
        {
            return aggregatePitchingStatHandler.getStats(id, batterId, batterSide, startDate, endDate, pitchingStatList)
        }
        else if (Period.PER_GAME.equals(convertedPeriod))
        {
            return perGamePitchingStatHandler.getStats(id, batterId, batterSide, startDate, endDate, pitchingStatList)
        }
        else
        {
            //TODO
            return perGamePitchingStatHandler.getStats(id, batterId, batterSide, startDate, endDate, pitchingStatList)
        }
    }
}