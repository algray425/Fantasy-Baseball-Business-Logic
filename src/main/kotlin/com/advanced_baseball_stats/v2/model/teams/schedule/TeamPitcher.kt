package com.advanced_baseball_stats.v2.model.teams.schedule

import kotlinx.serialization.Serializable

@Serializable
data class TeamPitcher(
    val name: String,
    val grade: Double
) {
}