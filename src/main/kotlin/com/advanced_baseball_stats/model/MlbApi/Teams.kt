package com.advanced_baseball_stats.model.MlbApi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Teams(
    @SerialName("away")
    val awayTeam: MlbTeam,
    @SerialName("home")
    val homeTeam: MlbTeam
)
{
}