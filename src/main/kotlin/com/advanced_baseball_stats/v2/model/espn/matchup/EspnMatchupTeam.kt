package com.advanced_baseball_stats.v2.model.espn.matchup

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class EspnMatchupTeam(
    @SerialName("cumulativeScore")
    val cumulativeScore: CumulativeScore,
    @SerialName("teamId")
    val teamId: Int
){
}