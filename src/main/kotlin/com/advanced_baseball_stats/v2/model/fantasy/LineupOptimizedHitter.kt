package com.advanced_baseball_stats.v2.model.fantasy

import kotlinx.serialization.Serializable

@Serializable
data class LineupOptimizedHitter(
    val playerId: String,
    val firstName: String,
    val lastName: String,
    val currentTeam: String,
    val currentPosition: String,
    val percentileOverall: Double,
    val runs: Int,
    val homeRuns: Int,
    val rbis: Int,
    val stolenBases: Int,
    val onBasePercentage: Double,
){
}