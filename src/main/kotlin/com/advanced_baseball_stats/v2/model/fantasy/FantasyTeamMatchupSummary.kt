package com.advanced_baseball_stats.v2.model.fantasy

import kotlinx.serialization.Serializable

@Serializable
class FantasyTeamMatchupSummary(
    val teamId: Int,
    val wins: Int,
    val losses: Int,
    val ties: Int,
    val runs: Int,
    val homeRuns: Int,
    val rbis: Int,
    val stolenBases: Int,
    val onBasePercentage: Double,
    val qualityStarts: Int,
    val savesPlusHolds: Int,
    val era: Double,
    val whip: Double,
    val ksPerNine: Double
){
}