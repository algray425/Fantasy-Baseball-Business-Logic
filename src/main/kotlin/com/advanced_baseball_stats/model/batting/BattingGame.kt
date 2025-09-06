package com.advanced_baseball_stats.model.batting

import com.advanced_baseball_stats.model.game.Game
import kotlinx.serialization.Serializable

@Serializable
data class BattingGame(
        val game    : Game
    ,   val stats   : MutableList<BattingGameStat> = mutableListOf()
)
{
}