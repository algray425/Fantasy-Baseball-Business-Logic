package com.advanced_baseball_stats.model.batting

import com.advanced_baseball_stats.model.player.MinimalPlayer

import kotlinx.serialization.Serializable

@Serializable
data class HolisticBattingStatList(
        val player      : MinimalPlayer
    ,   val games       : List<BattingGame>
)
{

}