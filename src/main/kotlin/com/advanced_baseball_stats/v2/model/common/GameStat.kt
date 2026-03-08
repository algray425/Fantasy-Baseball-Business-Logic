package com.advanced_baseball_stats.v2.model.common

import kotlinx.serialization.Serializable

@Serializable
class GameStat(
    val gameId: String,
    val date: String,
    val homeTeam: String,
    val awayTeam: String,
    val stat: Double
) {
}