package com.advanced_baseball_stats.v2.model.batters.FavoritePlayers

import kotlinx.serialization.Serializable

@Serializable
class FavoritePlayerInfo(
    val userId: String,
    val playerId: String
) {
}