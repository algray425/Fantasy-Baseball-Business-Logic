package com.advanced_baseball_stats.model.grades

import com.advanced_baseball_stats.model.game.UpcomingGame
import kotlinx.serialization.Serializable

@Serializable
sealed class HolisticGrade
{
    abstract val playerId       : String
    abstract val weekNumber     : Int
    abstract val num            : Double
    abstract val percentile     : Double
}