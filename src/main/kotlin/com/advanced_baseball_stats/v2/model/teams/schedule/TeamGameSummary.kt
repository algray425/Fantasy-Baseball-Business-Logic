package com.advanced_baseball_stats.v2.model.teams.schedule

import kotlinx.serialization.Serializable

@Serializable
data class TeamGameSummary(
    val teamName: String,
    val matchupGradeHitting: Double,
    val matchupGradeStartingPitching: Double,
    val matchupGradeReliefPitching: Double,
    val matchupGradeOverallPitching: Double,
) {
}