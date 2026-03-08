package com.advanced_baseball_stats.v2.model.espn.roster

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EspnPlayerEntry(
    @SerialName("playerId")
    val espnId: Int
){
}