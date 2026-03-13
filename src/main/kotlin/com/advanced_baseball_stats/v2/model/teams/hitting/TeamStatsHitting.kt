package com.advanced_baseball_stats.v2.model.teams.hitting

import kotlinx.serialization.Serializable

@Serializable
data class TeamStatsHitting(
    val team: String,
    val season: Int,
    val plateAppearances: Int,
    val atBats: Int,
    val runs: Int,
    val hits: Int,
    val singles: Int,
    val doubles: Int,
    val triples: Int,
    val homeRuns: Int,
    val rbis: Int,
    val sacHits: Int,
    val sacFlies: Int,
    val hitByPitch: Int,
    val walks: Int,
    val intentionalWalks: Int,
    val strikeOuts: Int,
    val stolenBases: Int,
    val caughtStealing: Int,
    val groundIntoDoublePlays: Int,
    val catcherInterference: Int,
    val battingAverage: Double,
    val onBasePercentage: Double,
    val sluggingPercentage: Double,
    val onBasePlusSlugging: Double,
    val eraAgainst: Double,
    val whipAgainst: Double,
    val ksPerNineAgainst: Double,
    val qualityStartsAgainst: Int,
    val savesAgainst: Int,
    val holdsAgainst: Int
)