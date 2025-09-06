package com.advanced_baseball_stats.model.game

import com.advanced_baseball_stats.model.database.TeamPitching
import kotlinx.serialization.Serializable

@Serializable
data class UpcomingGame(
    val date                    : String,
    val awayTeamPitching        : TeamPitching,
    val homeTeamPitching        : TeamPitching,
    val projectedAwayTeamPitcher: UpcomingPitcher? = null,
    val projectedHomeTeamPitcher: UpcomingPitcher? = null
)
{
}