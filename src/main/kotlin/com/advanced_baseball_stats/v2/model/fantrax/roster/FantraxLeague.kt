package com.advanced_baseball_stats.v2.model.fantrax.roster

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FanTraxLeague(
    @SerialName("rosters")
    val rosters: Map<String, FantraxRoster>
)
{
}