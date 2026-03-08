package com.advanced_baseball_stats.model.grades

import com.advanced_baseball_stats.model.player.MinimalPlayer

import kotlinx.serialization.Serializable

@Serializable
sealed class HolisticGrade
{
    abstract val player         : MinimalPlayer
    abstract val weekNumber     : Int
    abstract val num            : Double
    abstract val percentile     : Double
}