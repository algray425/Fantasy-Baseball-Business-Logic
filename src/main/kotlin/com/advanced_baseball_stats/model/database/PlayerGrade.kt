package com.advanced_baseball_stats.model.database

data class PlayerGrade(
        val retroPlayerId   : String
    ,   val mlbPlayerId     : String
    ,   val weekNumber      : Int
    ,   val weekStart       : String
    ,   val weekEnd         : String
    ,   val stat            : String
    ,   val number          : Double
    ,   val percentile      : Double
)
{
}