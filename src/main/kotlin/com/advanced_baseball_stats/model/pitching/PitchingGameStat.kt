package com.advanced_baseball_stats.model.pitching

import kotlinx.serialization.Serializable

@Serializable
data class PitchingGameStat(
        val statName: PitchingStat
    ,   val num     : Double
)
{
}