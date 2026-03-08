package com.advanced_baseball_stats.model.grades

import com.advanced_baseball_stats.model.player.MinimalPlayer

import kotlinx.serialization.Serializable

@Serializable
class HolisticPitchingGrade(
        override val player         : MinimalPlayer
    ,   override val weekNumber     : Int
    ,   val          statName       : PitchingGradeStat
    ,   override val num            : Double
    ,   override val percentile     : Double
) : HolisticGrade()
{
}