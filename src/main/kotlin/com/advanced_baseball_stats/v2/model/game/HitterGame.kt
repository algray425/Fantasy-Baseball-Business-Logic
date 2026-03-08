package com.advanced_baseball_stats.v2.model.game

import kotlinx.serialization.Serializable

@Serializable
class HitterGame(
    val opposingTeam                            : String,
    val venue                                   : String,
    val date                                    : String,
    val opposingTeamPitchingPercentileOverall   : Double?,
    val opposingPitcher                         : OpposingPitcherSummary?
) {
}