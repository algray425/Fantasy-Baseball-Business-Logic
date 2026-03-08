package com.advanced_baseball_stats.v2.model.game

import kotlinx.serialization.Serializable

@Serializable
class OpposingPitcherSummary(
    val playerId            : String,
    val firstName           : String,
    val lastName            : String,
    val percentileOverall   : Double
){
}