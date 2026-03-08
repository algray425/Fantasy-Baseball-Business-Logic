package com.advanced_baseball_stats.v2.model.espn.roster

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EspnTeam(
    @SerialName("id")
    val id: Int,
    @SerialName("roster")
    val roster: EspnRoster
){
}