package com.advanced_baseball_stats.model.grades

import com.advanced_baseball_stats.model.batting.BattingStat
import kotlinx.serialization.Serializable

@Serializable
class HolisticBattingGrade(
        override val playerId       : String
    ,   override val weekNumber     : Int
    ,            val statName       : BattingStat
    ,   override val num            : Double
    ,   override val percentile     : Double
) : HolisticGrade()
{
}