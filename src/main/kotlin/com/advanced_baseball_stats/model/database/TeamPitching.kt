package com.advanced_baseball_stats.model.database

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Rank

@Serializable
data class TeamPitching(
        val team            : String
    ,   val teamEra         : Double
    ,   val teamWhip        : Double
    ,   val teamHomeRuns    : Int
    ,   val teamStrikeouts  : Int
    ,   val rank            : Int
)
{
}