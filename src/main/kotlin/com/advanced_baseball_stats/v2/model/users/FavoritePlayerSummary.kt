package com.advanced_baseball_stats.v2.model.users

import kotlinx.serialization.Serializable

@Serializable
class FavoritePlayerSummary(
    val playerId    : String,
    val firstName   : String,
    val lastName    : String,
    val position    : String,
    val team        : String
) {
}