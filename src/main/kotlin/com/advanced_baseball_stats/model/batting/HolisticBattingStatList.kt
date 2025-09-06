package com.advanced_baseball_stats.model.batting

import kotlinx.serialization.Serializable

@Serializable
data class HolisticBattingStatList(
        val playerId    : String
    ,   val games       : List<BattingGame>
)
{

}