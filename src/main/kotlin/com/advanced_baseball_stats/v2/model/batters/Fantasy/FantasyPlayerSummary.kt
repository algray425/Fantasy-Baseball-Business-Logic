package com.advanced_baseball_stats.v2.model.batters.Fantasy

import kotlinx.serialization.Serializable

@Serializable
sealed class FantasyPlayerSummary
{
    abstract val playerId: String
    abstract val firstName: String
    abstract val lastName: String
    abstract val currentPosition: String
    abstract val currentTeam: String
    abstract val percentileOverall: Double
    abstract val percentileQualified: Double
}