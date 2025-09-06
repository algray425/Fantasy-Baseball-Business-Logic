package com.advanced_baseball_stats.repository.tables

import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.float
import org.ktorm.schema.varchar

object WeeklyGradesTable : Table<Nothing>("weekly_grades")
{
    val weekNumber = int("week_number")
    val weekStart = varchar("week_start")
    val weekEnd = varchar("week_end")
    val playerId = varchar("player_id")
    val stat = varchar("stat")
    val num = float("num")
    val percentile = float("percentile")
    val weekChange = float("week_change")
    val rank = int("rank")
    val season = int("season")
}