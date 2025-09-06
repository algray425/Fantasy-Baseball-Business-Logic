package com.advanced_baseball_stats.model.game

import com.advanced_baseball_stats.model.common.Team
import kotlinx.serialization.Serializable

@Serializable
data class Game (val gid: String, val temp: Int = 0, val windSpeed : Int = 0, val windDirection : WindDirection = WindDirection.UNKNOWN,
                 val condition: Condition = Condition.UNKNOWN, val precipitation: Precipitation = Precipitation.UNKNOWN,
                 val timeOfDay: TimeOfDay = TimeOfDay.UNKNOWN, val homeTeam: Team = Team.UNKNOWN, val awayTeam: Team = Team.UNKNOWN
) {
}