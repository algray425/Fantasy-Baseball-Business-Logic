package com.advanced_baseball_stats.v2.model.espn.matchup

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class CumulativeScore(
    @SerialName("scoreByStat")
    val scoreByStat: EspnScoreByStat,
    @SerialName("wins")
    val wins: Int,
    @SerialName("losses")
    val losses: Int,
    @SerialName("ties")
    val ties: Int
){
}