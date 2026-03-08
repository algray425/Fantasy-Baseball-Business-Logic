package com.advanced_baseball_stats.v2.model.batters

import com.advanced_baseball_stats.v2.model.game.HitterGame

import kotlinx.serialization.Serializable

@Serializable
class BatterSummary(
    val playerId: String,
    val firstName: String,
    val lastName: String,
    val dob: Int,
    val batSide: String,
    val throwHand: String,
    val height: String,
    val weight: Double,
    val currentTeam: String,
    val currentPosition: String,
    val predictedRuns: Int,
    val predictedHomeRuns: Int,
    val predictedRbis: Int,
    val predictedStolenBases: Int,
    val predictedObp: Double,
    val overallPercentilePredictedRuns: Double,
    val overallPercentilePredictedHomeRuns: Double,
    val overallPercentilePredictedRbis: Double,
    val overallPercentilePredictedStolenBases: Double,
    val overallPercentilePredictedObp: Double,
    val overallPercentilePredictedGrade: Double,
    val qualifiedPercentilePredictedRuns: Double,
    val qualifiedPercentilePredictedHomeRuns: Double,
    val qualifiedPercentilePredictedRbis: Double,
    val qualifiedPercentilePredictedStolenBases: Double,
    val qualifiedPercentilePredictedObp: Double,
    val qualifiedPercentilePredictedGrade: Double,
    val upcomingSchedule: MutableList<HitterGame> = mutableListOf()
) {
    fun addGame(game: HitterGame)
    {
        this.upcomingSchedule.add(game)
    }
}