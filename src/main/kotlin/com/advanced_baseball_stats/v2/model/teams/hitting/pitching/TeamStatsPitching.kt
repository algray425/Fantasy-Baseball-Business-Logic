package com.advanced_baseball_stats.v2.model.teams.hitting.pitching

import kotlinx.serialization.Serializable

@Serializable
data class TeamStatsPitching(
    val team: String,
    val season: Int,
    val ipOuts: Int,
    val battersFaced: Int,
    val hits: Int,
    val singles: Int,
    val doubles: Int,
    val triples: Int,
    val homeRuns: Int,
    val runs: Int,
    val earnedRuns: Int,
    val walks: Int,
    val intentionalWalks: Int,
    val strikeOuts: Int,
    val hitByPitch: Int,
    val wildPitches: Int,
    val balks: Int,
    val sacHits: Int,
    val sacFlies: Int,
    val stolenBases: Int,
    val caughtStealing: Int,
    val passedBalls: Int,
    val era: Double,
    val whip: Double,
    val ksPerNine: Double,
    val rbisAgainst: Int,
    val onBasePercentageAgainst: Double
)