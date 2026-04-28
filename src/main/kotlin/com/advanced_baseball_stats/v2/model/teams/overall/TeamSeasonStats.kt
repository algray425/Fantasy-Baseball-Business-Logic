package com.advanced_baseball_stats.v2.model.teams.overall

import com.advanced_baseball_stats.v2.model.batters.SeasonRankedBatter
import com.advanced_baseball_stats.v2.model.pitchers.SeasonRankedReliefPitcher
import com.advanced_baseball_stats.v2.model.pitchers.SeasonRankedStartingPitcher
import com.advanced_baseball_stats.v2.model.teams.schedule.TeamSchedule
import kotlinx.serialization.Serializable

@Serializable
data class TeamSeasonStats(
    val team: String,
    val season: Int,
    // Pitching stats
    val runs: Int,
    val homeRuns: Int,
    val rbisAgainst: Int,
    val stolenBases: Int,
    val onBasePercentageAgainst: Double,
    // Hitting stats
    val eraAgainst: Double,
    val whipAgainst: Double,
    val ksPerNineAgainst: Double,
    val qualityStartsAgainst: Int,
    val savesAgainst: Int,
    val holdsAgainst: Int,
    // Hitting grades
    val percentileEra: Double,
    val percentileWhip: Double,
    val percentileKsPerNine: Double,
    val percentileQualityStarts: Double,
    val percentileSavesAndHolds: Double,
    val percentileStartingPitchers: Double,
    val percentileReliefPitchers: Double,
    val percentileHittingOverall: Double,
    // Pitching grades
    val percentileRuns: Double,
    val percentileHomeRuns: Double,
    val percentileRbis: Double,
    val percentileStolenBases: Double,
    val percentileOnBasePercentage: Double,
    val percentilePitchingOverall: Double,
    //Batters on roster
    var teamBatters: List<SeasonRankedBatter> = listOf(),
    //Starting pitchers on roster
    var teamStartingPitchers: List<SeasonRankedStartingPitcher> = listOf(),
    //Relief pitchers on roster
    var teamReliefPitchers: List<SeasonRankedReliefPitcher> = listOf(),
    //upcoming matchups
    var teamSchedule: TeamSchedule? = null
)