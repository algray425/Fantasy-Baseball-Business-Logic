package com.advanced_baseball_stats.v2.model.fantrax.roster

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FantraxRoster(
    @SerialName("teamName")
    val teamName: String,
    @SerialName("rosterItems")
    val players: List<FantraxPlayer>
) {
}