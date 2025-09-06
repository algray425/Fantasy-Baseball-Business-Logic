package com.advanced_baseball_stats.model.batting

import kotlinx.serialization.Serializable

@Serializable
data class BattingGameStat(
        val statName: BattingStat
    ,   val num     : Double
)
{
}