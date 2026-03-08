package com.advanced_baseball_stats.v2.model.batters

import kotlinx.serialization.Serializable

@Serializable
class BatterProjection(
    val playerId: String,
    val firstName: String,
    val lastName: String,
    val team: String,
    val position: String,
    val runs: Int,
    val homeRuns: Int,
    val rbis: Int,
    val stolenBases: Int,
    val onBasePercentage: Double,
    val overallPercentileRuns: Double,
    val overallPercentileHomeRuns: Double,
    val overallPercentileRbis: Double,
    val overallPercentileStolenBases: Double,
    val overallPercentileOnBasePercentage: Double,
    val overallPercentileGrade: Double,
    val qualifiedPercentileRuns: Double,
    val qualifiedPercentileHomeRuns: Double,
    val qualifiedPercentileRbis: Double,
    val qualifiedPercentileStolenBases: Double,
    val qualifiedPercentileOnBasePercentage: Double,
    val qualifiedPercentileGrade: Double
){
}