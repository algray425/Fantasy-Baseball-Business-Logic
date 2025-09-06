package com.advanced_baseball_stats.model.MlbApi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LeagueRecord(
    @SerialName("wins")
    val wins: Int,
    @SerialName("losses")
    val losses: Int,
    @SerialName("pct")
    val winPercentage: String
)
{
}