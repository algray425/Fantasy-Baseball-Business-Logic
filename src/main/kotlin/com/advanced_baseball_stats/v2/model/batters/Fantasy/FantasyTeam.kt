package com.advanced_baseball_stats.v2.model.batters.Fantasy

import kotlinx.serialization.Serializable

@Serializable
class FantasyTeam(
    val teamId      : String,
    val teamName    : String,
    val leagueId    : String,
    val leagueName  : String,
    val leagueType  : String
){
}