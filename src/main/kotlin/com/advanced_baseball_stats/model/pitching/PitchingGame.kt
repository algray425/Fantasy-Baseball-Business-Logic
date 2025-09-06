package com.advanced_baseball_stats.model.pitching

import com.advanced_baseball_stats.model.game.Game
import kotlinx.serialization.Serializable

@Serializable
data class PitchingGame(
        val game    : Game
    ,   val stats   : MutableList<PitchingGameStat> = mutableListOf()
)
{
}