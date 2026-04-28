package com.advanced_baseball_stats.v2.data

import com.advanced_baseball_stats.v2.model.fantasy.HolisiticFantasyLeague

interface FantasyLeagueDataSource
{
    suspend fun getFantasyTeamRosters(leagueId: String): HolisiticFantasyLeague?
}