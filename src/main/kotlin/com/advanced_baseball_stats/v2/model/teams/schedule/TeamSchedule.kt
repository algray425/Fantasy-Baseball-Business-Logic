package com.advanced_baseball_stats.v2.model.teams.schedule

import kotlinx.serialization.Serializable

@Serializable
data class TeamSchedule(
    val games: List<TeamGame>
)
{
}