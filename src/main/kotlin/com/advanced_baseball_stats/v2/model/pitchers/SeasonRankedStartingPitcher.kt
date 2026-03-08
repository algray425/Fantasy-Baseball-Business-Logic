package com.advanced_baseball_stats.v2.model.pitchers

import kotlinx.serialization.Serializable

@Serializable
class SeasonRankedStartingPitcher(
    val playerId: String,
    val firstName: String,
    val lastName: String,
    val team: String,
    val grade: Double,
    val qualityStarts: Int,
    val era: Double,
    val whip: Double,
    val ksPerNine: Double
){
}