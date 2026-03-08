package com.advanced_baseball_stats.v2.model.batters.Fantasy

import kotlinx.serialization.Serializable

@Serializable
class FantasyPlayerSummaryStartingPitching(
    override val playerId: String,
    override val firstName: String,
    override val lastName: String,
    override val currentPosition: String,
    override val currentTeam: String,
    override val percentileOverall: Double,
    override val percentileQualified: Double,
    val percentileOverallQualityStarts: Double,
    val percentileOverallEra: Double,
    val percentileOverallWhip: Double,
    val percentileOverallKsPerNine: Double,
    val qualityStarts: Int,
    val era: Double,
    val whip: Double,
    val ksPerNine: Double
) : FantasyPlayerSummary()
{
}