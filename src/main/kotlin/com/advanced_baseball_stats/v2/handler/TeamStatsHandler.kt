package com.advanced_baseball_stats.v2.handler

import com.advanced_baseball_stats.v2.exception.InvalidTeamStatsHittingRequest
import com.advanced_baseball_stats.v2.exception.InvalidTeamStatsPitchingRequest
import com.advanced_baseball_stats.v2.model.teams.hitting.TeamStatsHitting
import com.advanced_baseball_stats.v2.model.teams.hitting.pitching.TeamStatsPitching
import com.advanced_baseball_stats.v2.repository.stats.TeamHittingSql
import com.advanced_baseball_stats.v2.repository.stats.TeamPitchingSql

class TeamStatsHandler
{
    fun getTeamHittingStatsPerSeason(teamId: String, season: Int): TeamStatsHitting
    {
        val teamStatsHitting: TeamStatsHitting = TeamHittingSql.getTeamHittingStatsPerSeason(teamId, season)
            ?: throw InvalidTeamStatsHittingRequest("Invalid request arguments for team stats hitting")

        return teamStatsHitting
    }

    fun getTeamPitchingStatsPerSeason(teamId: String, season: Int): TeamStatsPitching
    {
        val teamStatsPitching: TeamStatsPitching = TeamPitchingSql.getTeamPitchingStats(teamId, season)
            ?: throw InvalidTeamStatsPitchingRequest("Invalid request arguments for team stats pitching")

        return teamStatsPitching
    }
}