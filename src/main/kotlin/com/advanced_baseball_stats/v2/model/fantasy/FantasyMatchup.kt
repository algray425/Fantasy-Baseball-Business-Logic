package com.advanced_baseball_stats.v2.model.fantasy

import kotlinx.serialization.Serializable

@Serializable
class FantasyMatchup(
    val homeTeamSummary: FantasyTeamMatchupSummary,
    val awayTeamSummary: FantasyTeamMatchupSummary,
) {
}