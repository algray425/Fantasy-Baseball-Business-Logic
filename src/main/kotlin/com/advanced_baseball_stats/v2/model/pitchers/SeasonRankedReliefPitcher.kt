package com.advanced_baseball_stats.v2.model.pitchers

import kotlinx.serialization.Serializable

@Serializable
class SeasonRankedReliefPitcher(
    val playerId: String,
    val firstName: String,
    val lastName: String,
    val team: String,
    val grade: Double,
    val saves: Int,
    val holds: Int,
    val era: Double,
    val whip: Double,
    val ksPerNine: Double
) {
}