package com.advanced_baseball_stats.v2.model.espn.matchup

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class EspnStat(
    @SerialName("result")
    val result: String,
    @SerialName("score")
    val score: Double,
){
}