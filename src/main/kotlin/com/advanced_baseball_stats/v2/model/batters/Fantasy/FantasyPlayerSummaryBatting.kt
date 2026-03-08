package com.advanced_baseball_stats.v2.model.batters.Fantasy

import kotlinx.serialization.Serializable

@Serializable
class FantasyPlayerSummaryBatting(
    override val playerId: String,
    override val firstName: String,
    override val lastName: String,
    override val currentPosition: String,
    override val currentTeam: String,
    override val percentileOverall: Double,
    override val percentileQualified: Double,
    val percentileOverallRuns: Double,
    val percentileOverallHomeRuns: Double,
    val percentileOverallRbis: Double,
    val percentileOverallStolenBases: Double,
    val percentileOverallOnBasePercentage: Double,
    val runs: Int,
    val homeRuns: Int,
    val rbis: Int,
    val stolenBases: Int,
    val onBasePercentage: Double
): FantasyPlayerSummary()
{
}