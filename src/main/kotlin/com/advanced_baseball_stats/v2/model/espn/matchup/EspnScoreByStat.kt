package com.advanced_baseball_stats.v2.model.espn.matchup

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class EspnScoreByStat(
    @SerialName("20")
    val runsStat: EspnStat,
    @SerialName("5")
    val homeRunsStat: EspnStat,
    @SerialName("21")
    val rbisStat: EspnStat,
    @SerialName("23")
    val stolenBasesStat: EspnStat,
    @SerialName("17")
    val obpStat: EspnStat,
    @SerialName("63")
    val qualityStartsStat: EspnStat,
    @SerialName("83")
    val savesAndHoldsStat: EspnStat,
    @SerialName("47")
    val eraStat: EspnStat,
    @SerialName("41")
    val whipStat: EspnStat,
    @SerialName("49")
    val ksPerNineStat: EspnStat,
){
}