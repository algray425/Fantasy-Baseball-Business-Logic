package com.advanced_baseball_stats.utility.converter.date

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DateHelper
{
    fun getCurrentDate(): String
    {
        val curDate             = LocalDate.now()
        val dateTimeFormatter   = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        return dateTimeFormatter.format(curDate)
    }
}