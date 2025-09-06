package com.advanced_baseball_stats.repository.tables

import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.float
import org.ktorm.schema.varchar

object AggregateWeeklyGradesTable : Table<Nothing>("aggregate_weekly_grades")
{
    val weekNumber      = int       ("week_number"  )
    val weekStart       = varchar   ("week_start"   )
    val weekEnd         = varchar   ("week_end"     )
    val playerId        = varchar   ("player_id"    )
    val stat            = varchar   ("stat"         )
    val aggregate_num   = float     ("aggregate_num")
    val percentile      = float     ("percentile"   )
}