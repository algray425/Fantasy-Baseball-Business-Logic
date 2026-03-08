package com.advanced_baseball_stats.v2.model.pitchers

import com.advanced_baseball_stats.v2.model.game.PitcherGame

import kotlinx.serialization.Serializable

@Serializable
class PitcherSummary(
    val playerId: String,
    val firstName: String,
    val lastName: String,
    val dob: Int,
    val batSide: String,
    val throwHand: String,
    val height: String,
    val weight: Double,
    val currentTeam: String,
    val upcomingSchedule: MutableList<PitcherGame> = mutableListOf()
) {
    fun addGame(game: PitcherGame)
    {
        this.upcomingSchedule.add(game)
    }
}