package com.advanced_baseball_stats.model.game

import kotlinx.serialization.Serializable

@Serializable
data class UpcomingPitcher(
        val name        : String
    ,   val era         : Double
    ,   val whip        : Double
    ,   val homeRuns    : Int
    ,   val strikeouts  : Int
)
{
}