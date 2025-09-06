package com.advanced_baseball_stats.model.pitching

import kotlinx.serialization.Serializable

@Serializable
class HolisticPitchingStatList(
        val playerId: String
    ,   val games   : List<PitchingGame>
)
{
}