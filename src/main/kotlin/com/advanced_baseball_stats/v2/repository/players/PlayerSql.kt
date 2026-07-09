package com.advanced_baseball_stats.v2.repository.players

import com.advanced_baseball_stats.v2.model.playerSearch.PlayerSearchInfo
import com.advanced_baseball_stats.v2.repository.stats.DatabaseConnection
import com.advanced_baseball_stats.v2.repository.stats.tables.BiosTable
import com.advanced_baseball_stats.v2.repository.stats.tables.SeasonStatsHittingTable
import com.advanced_baseball_stats.v2.repository.stats.tables.SeasonStatsPitchingTable
import org.ktorm.dsl.*

object PlayerSql
{
    fun getSearchableBatters(season: Int): List<PlayerSearchInfo>
    {
        val searchableBatters = mutableListOf<PlayerSearchInfo>()

        DatabaseConnection.database.from(SeasonStatsHittingTable)
            .innerJoin(BiosTable, on = SeasonStatsHittingTable.playerId eq BiosTable.playerId)
            .select(BiosTable.firstName, BiosTable.lastName, BiosTable.playerId, BiosTable.currentPosition, BiosTable.currentTeam)
            .where(SeasonStatsHittingTable.season eq season)
            .forEach { batterRow ->
                val firstName       = batterRow[BiosTable.firstName         ] ?: ""
                val lastName        = batterRow[BiosTable.lastName          ] ?: ""
                val playerId        = batterRow[BiosTable.playerId          ] ?: ""
                val currentPosition = batterRow[BiosTable.currentPosition   ] ?: ""
                val currentTeam     = batterRow[BiosTable.currentTeam       ] ?: ""

                val fullName = "$firstName $lastName"

                searchableBatters.add(PlayerSearchInfo(fullName, playerId, currentTeam, currentPosition))
            }

        return searchableBatters
    }

    fun getSearchablePitchers(season: Int): List<PlayerSearchInfo>
    {
        val searchablePitchers = mutableListOf<PlayerSearchInfo>()

        DatabaseConnection.database.from(SeasonStatsPitchingTable)
            .innerJoin(BiosTable, on = SeasonStatsPitchingTable.playerId eq BiosTable.playerId)
            .select(BiosTable.firstName, BiosTable.lastName, BiosTable.currentPosition, BiosTable.currentTeam)
            .where(SeasonStatsPitchingTable.season eq season)
            .forEach { batterRow ->
                val firstName       = batterRow[BiosTable.firstName         ] ?: ""
                val lastName        = batterRow[BiosTable.lastName          ] ?: ""
                val playerId        = batterRow[BiosTable.playerId          ] ?: ""
                val currentPosition = batterRow[BiosTable.currentPosition   ] ?: ""
                val currentTeam     = batterRow[BiosTable.currentTeam       ] ?: ""

                val fullName = "$firstName $lastName"

                searchablePitchers.add(PlayerSearchInfo(fullName, playerId, currentTeam, currentPosition))
            }

        return searchablePitchers
    }
}