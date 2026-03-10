package com.advanced_baseball_stats.v2.model.pitchers

import kotlinx.serialization.Serializable

@Serializable
class StartingPitcherProjection(
    val playerId: String,
    val firstName: String,
    val lastName: String,
    val team: String,
    val qualityStarts: Int,
    val era: Double,
    val whip: Double,
    val ksPerNine: Double,
    val grade: Double,
    val percentileQualityStarts: Double,
    val percentileEra: Double,
    val percentileWhip: Double,
    val percentileKsPerNine: Double,
    val percentileGrade: Double,

){
}