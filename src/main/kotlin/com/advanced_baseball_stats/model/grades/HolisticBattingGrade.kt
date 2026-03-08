package com.advanced_baseball_stats.model.grades

import com.advanced_baseball_stats.model.batting.BattingStat
import com.advanced_baseball_stats.model.player.MinimalPlayer

import kotlinx.serialization.Serializable

@Serializable
class HolisticBattingGrade(
        override val player         : MinimalPlayer
    ,   override val weekNumber     : Int
    ,            val statName       : BattingStat
    ,   override val num            : Double
    ,   override val percentile     : Double
) : HolisticGrade()
{
}