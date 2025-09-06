package com.advanced_baseball_stats.model.grades

import com.advanced_baseball_stats.model.game.UpcomingGame
import kotlinx.serialization.Serializable

@Serializable
data class PerGamePercentileGradesResponse(
    val holisticGrade: HolisticGrade,
    val upcomingGames: List<UpcomingGame>
)
{
}