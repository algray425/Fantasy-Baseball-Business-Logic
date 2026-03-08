package com.advanced_baseball_stats.v2.model.batters

import kotlinx.serialization.Serializable

@Serializable
class SeasonRankedBatter(
    val playerId: String,
    val firstName: String,
    val lastName: String,
    val team: String,
    val position: String,
    val grade: Double,
    val runs: Int,
    val homeRuns: Int,
    val rbis: Int,
    val stolenBases: Int,
    val onBasePercentage: Double
)
{

}