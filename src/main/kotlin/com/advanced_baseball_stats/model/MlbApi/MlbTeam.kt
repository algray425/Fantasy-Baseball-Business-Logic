package com.advanced_baseball_stats.model.MlbApi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MlbTeam(
    @SerialName("leagueRecord")
    val leagueRecord: LeagueRecord,
    @SerialName("team")
    val team: TeamInfo,
    @SerialName("probablePitcher")
    val probablePitcher: ProbablePitcher? = null,
    @SerialName("splitSquad")
    val splitSquad: Boolean,
    @SerialName("seriesNumber")
    val seriesNumber: Int
)
{
}