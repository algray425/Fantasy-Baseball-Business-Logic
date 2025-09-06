package com.advanced_baseball_stats.model.MlbApi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Schedule (
    @SerialName("copyright")
    val copyright: String,
    @SerialName("totalItems")
    val totalItems: Int,
    @SerialName("totalEvents")
    val totalEvents: Int,
    @SerialName("totalGames")
    val totalGames: Int,
    @SerialName("totalGamesInProgress")
    val totalGamesInProgress: Int,
    @SerialName("dates")
    val dates: List<Dates>
)
{
}