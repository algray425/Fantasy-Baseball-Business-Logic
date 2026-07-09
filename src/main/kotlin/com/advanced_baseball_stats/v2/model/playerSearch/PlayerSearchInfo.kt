package com.advanced_baseball_stats.v2.model.playerSearch

import kotlinx.serialization.Serializable

@Serializable
class PlayerSearchInfo(
    val playerName: String,
    val playerId: String,
    val playerTeam: String,
    val playerPosition: String
) {
}