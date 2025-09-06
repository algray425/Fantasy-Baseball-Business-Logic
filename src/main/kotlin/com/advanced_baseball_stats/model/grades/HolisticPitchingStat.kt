package com.advanced_baseball_stats.model.grades

import kotlinx.serialization.Serializable

@Serializable
class HolisticPitchingStat(
        override val playerId       : String
    ,   override val weekNumber     : Int
    ,            val statName       : PitchingGradeStat
    ,   override val num            : Double
    ,   override val percentile     : Double
) : HolisticGrade()
{
}