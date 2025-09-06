package com.advanced_baseball_stats.handler.pitching

import com.advanced_baseball_stats.model.pitching.HolisticPitchingStatList
import com.advanced_baseball_stats.model.pitching.PitchingStat
import com.advanced_baseball_stats.repository.PlayerPitchingSql
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PerGamePitchingStatHandler
{
    fun getCurrentDate(): String
    {
        val curDate             = LocalDate.now()
        val dateTimeFormatter   = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        return dateTimeFormatter.format(curDate)
    }

    fun getStats(id: String, batterId: String, batterSide: String, startDate: String, endDate: String, statList: List<PitchingStat>): HolisticPitchingStatList
    {
        val finalEndDate = if (endDate.isEmpty()) this.getCurrentDate() else endDate

        return PlayerPitchingSql.getPitchingStatPerGame(id, startDate, finalEndDate, statList)
    }
}