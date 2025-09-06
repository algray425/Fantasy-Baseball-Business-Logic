package com.advanced_baseball_stats.model.database

import kotlinx.serialization.Serializable

@Serializable
data class PlayerPitching(
        val id          : String
    ,   val era         : Double
    ,   val whip        : Double
    ,   val homeRuns    : Int
    ,   val strikeouts  : Int
)
{
}