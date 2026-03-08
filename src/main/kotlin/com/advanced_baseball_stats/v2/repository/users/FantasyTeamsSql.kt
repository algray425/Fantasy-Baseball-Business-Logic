package com.advanced_baseball_stats.v2.repository.users

import com.advanced_baseball_stats.v2.model.batters.Fantasy.FantasyTeam
import com.advanced_baseball_stats.v2.repository.users.tables.FantasyTeamsTable

import org.ktorm.dsl.*

object FantasyTeamsSql
{
    fun getFantasyTeamsForUser(userId: String): MutableList<FantasyTeam>
    {
        val fantasyTeams = mutableListOf<FantasyTeam>()

        UserDatabaseConnection.database.from(FantasyTeamsTable)
            .select(FantasyTeamsTable.teamId, FantasyTeamsTable.teamName, FantasyTeamsTable.leagueName, FantasyTeamsTable.leagueId, FantasyTeamsTable.leagueType)
            .where { FantasyTeamsTable.userId eq userId }
            .forEach { teamRow ->
                val teamId      = teamRow[FantasyTeamsTable.teamId]
                val teamName    = teamRow[FantasyTeamsTable.teamName]
                val leagueId    = teamRow[FantasyTeamsTable.leagueId]
                val leagueName  = teamRow[FantasyTeamsTable.leagueName]
                val leagueType  = teamRow[FantasyTeamsTable.leagueType]

                if (teamId != null && teamName != null && leagueId != null && leagueName != null && leagueType != null)
                {
                    fantasyTeams.add(FantasyTeam(teamId, teamName, leagueId, leagueName, leagueType))
                }
            }
            return fantasyTeams
    }
}