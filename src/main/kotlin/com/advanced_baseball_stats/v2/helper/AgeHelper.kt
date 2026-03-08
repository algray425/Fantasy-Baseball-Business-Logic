package com.advanced_baseball_stats.v2.helper

import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneOffset

object AgeHelper
{
    fun calculateAgeFromTimestamp(timeStampSeconds: Int): Int
    {
        val birthDate = Instant.ofEpochSecond(timeStampSeconds.toLong())
            .atZone(ZoneOffset.UTC)
            .toLocalDate()

        val currentDate = LocalDate.now()

        val period = Period.between(birthDate, currentDate)

        return period.years
    }

}