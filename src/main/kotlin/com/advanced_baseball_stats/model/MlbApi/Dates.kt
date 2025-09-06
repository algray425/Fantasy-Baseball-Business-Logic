package com.advanced_baseball_stats.model.MlbApi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Dates(
    @SerialName("date")
    val date: String,
    @SerialName("totalItems")
    val totalItems: Int,
    @SerialName("totalEvents")
    val totalEvents: Int,
    @SerialName("totalGames")
    val totalGames: Int,
    @SerialName("totalGamesInProgress")
    val totalGamesInProgress: Int,
    @SerialName("games")
    val games: List<MlbGame>
)
{
}