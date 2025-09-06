package com.advanced_baseball_stats.model.MlbApi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Status(
    @SerialName("abstractGameState")
    val abstractGameState: String,
    @SerialName("codedGameState")
    val codedGameState: String,
    @SerialName("detailedState")
    val detailedState: String,
    @SerialName("statusCode")
    val statusCode: String,
    @SerialName("startTimeTBD")
    val startTimeTBD: Boolean,
    @SerialName("abstractGameCode")
    val abstractGameCode: String
)
{
}