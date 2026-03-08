package com.advanced_baseball_stats.v2.model.batters

import kotlinx.serialization.Serializable

@Serializable
class BatterSeasonSummary(
    val season: Int,
    val teams: String,
    val plateAppearances: Int,
    val atBats: Int,
    val runs: Int,
    val hits: Int,
    val doubles: Int,
    val triples: Int,
    val homeRuns: Int,
    val rbis: Int,
    val walks: Int,
    val strikeOuts: Int,
    val stolenBases: Int,
    val battingAverage: Double,
    val onBasePercentage: Double,
    val sluggingPercentage: Double,
    val onBasePlusSlugging: Double,
    val babip: Double,
    val spd: Double,
    val groundBallPercentage: Double,
    val flyBallPercentage: Double,
    val lineDrivePercentage: Double,
    val popUpPercentage: Double,
    val hardHitPercentage: Double,
    val barrelPercentage: Double,
){
}