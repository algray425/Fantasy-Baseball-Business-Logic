package com.advanced_baseball_stats.v2.model.espn.matchup

import com.advanced_baseball_stats.v2.helper.derserializer.InfinityAwareDoubleSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class EspnStat(
    @SerialName("result")
    val result: String,
    @SerialName("score")
    @Serializable(with = InfinityAwareDoubleSerializer::class)
    val score: Double,
){
}