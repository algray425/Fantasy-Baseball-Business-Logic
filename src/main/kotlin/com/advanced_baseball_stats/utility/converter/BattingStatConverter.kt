package com.advanced_baseball_stats.utility.converter

import com.advanced_baseball_stats.exception.UnknownBattingStatException
import com.advanced_baseball_stats.model.batting.BattingStat

object BattingStatConverter
{
    fun convertBattingStat(battingStat: String): BattingStat
    {
        val convertedBattingStat: BattingStat

        try
        {
            convertedBattingStat = BattingStat.valueOf(battingStat)
        }
        catch (ex: IllegalArgumentException)
        {
            throw UnknownBattingStatException("$battingStat is not a valid batting stat!")
        }
        return convertedBattingStat
    }
}