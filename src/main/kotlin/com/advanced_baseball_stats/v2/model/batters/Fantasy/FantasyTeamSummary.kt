package com.advanced_baseball_stats.v2.model.batters.Fantasy

import com.advanced_baseball_stats.v2.model.batters.SeasonRankedBatter
import com.advanced_baseball_stats.v2.model.fantasy.FantasyMatchup
import com.advanced_baseball_stats.v2.model.pitchers.SeasonRankedReliefPitcher
import com.advanced_baseball_stats.v2.model.pitchers.SeasonRankedStartingPitcher

import kotlinx.serialization.Serializable

@Serializable
class FantasyTeamSummary(
    val rosterGrades                    : List<FantasyPlayerSummary>,
    val currentMatchup                  : FantasyMatchup?,
    val upcomingOpponent                : List<FantasyPlayerSummary>,
    val bestAvailableHitters            : List<SeasonRankedBatter>,
    val bestAvailableStartingPitchers   : List<SeasonRankedStartingPitcher>,
    val bestAvailableReliefPitchers     : List<SeasonRankedReliefPitcher>,
)
{
}