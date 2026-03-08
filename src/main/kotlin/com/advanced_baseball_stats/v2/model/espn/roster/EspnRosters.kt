package com.advanced_baseball_stats.v2.model.espn.roster

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EspnRosters(
    @SerialName("teams")
    val teams: List<EspnTeam>
){
}