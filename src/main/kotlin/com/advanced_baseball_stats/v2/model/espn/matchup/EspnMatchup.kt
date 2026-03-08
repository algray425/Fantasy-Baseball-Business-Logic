package com.advanced_baseball_stats.v2.model.espn.matchup

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class EspnMatchup(
    @SerialName("away")
    val awayTeam: EspnMatchupTeam,
    @SerialName("home")
    val homeTeam: EspnMatchupTeam,
    @SerialName("matchupPeriodId")
    val matchupPeriodId: Int
){
}