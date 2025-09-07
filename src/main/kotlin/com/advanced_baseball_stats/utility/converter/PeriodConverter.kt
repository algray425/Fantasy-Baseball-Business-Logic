package com.advanced_baseball_stats.utility.converter

import com.advanced_baseball_stats.exception.UnknownPeriodException
import com.advanced_baseball_stats.model.common.Period

object PeriodConverter
{
    fun convertPeriod(period: String): Period
    {
        val convertedPeriod: Period

        try
        {
            convertedPeriod = Period.valueOf(period.uppercase())
        }
        catch (ex: IllegalArgumentException)
        {
            throw UnknownPeriodException("$period is not a valid period! Valid period inputs are PER_GAME, AGGREGATE and TOTAL")
        }

        return convertedPeriod
    }
}