package com.advanced_baseball_stats.v2.model.game

import kotlinx.serialization.Serializable

@Serializable
class PitcherGame(
    val opposingTeam                            : String,
    val venue                                   : String,
    val date                                    : String,
    val starting                                : Boolean,
    val opposingTeamHittingPercentileOverall    : Double?,
) {
}