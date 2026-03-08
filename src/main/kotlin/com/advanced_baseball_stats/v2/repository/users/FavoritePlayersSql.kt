package com.advanced_baseball_stats.v2.repository.users

import com.advanced_baseball_stats.v2.model.users.FavoritePlayerSummary
import com.advanced_baseball_stats.v2.repository.stats.DatabaseConnection
import com.advanced_baseball_stats.v2.repository.stats.tables.BiosTable
import com.advanced_baseball_stats.v2.repository.users.tables.FavoritePlayersTable

import org.ktorm.dsl.*

object FavoritePlayersSql
{
    fun getFavoritePlayersIds(userId: String): MutableList<String>
    {
        val playerIds = mutableListOf<String>();

        UserDatabaseConnection.database.from(FavoritePlayersTable)
            .select(FavoritePlayersTable.playerId)
            .where { FavoritePlayersTable.userId eq userId }
            .forEach { playerRow ->
                val playerId = playerRow[FavoritePlayersTable.playerId]

                if (playerId != null)
                {
                    playerIds.add(playerId)
                }
            }
        return playerIds
    }

    fun addFavoritePlayer(userId: String, playerId: String)
    {
        val rowKey = userId + playerId

        UserDatabaseConnection.database.insert(FavoritePlayersTable)
        {
            set(FavoritePlayersTable.key, rowKey)
            set(FavoritePlayersTable.playerId, playerId)
            set(FavoritePlayersTable.userId, userId)
        }
    }

    fun removeFavoritePlayer(userId: String, playerId: String)
    {
        UserDatabaseConnection.database.delete(FavoritePlayersTable)
        {
            (it.userId eq userId) and (it.playerId eq playerId)
        }
    }

    fun getFavoritePlayerSummaries(favoritePlayerIds: MutableList<String>): MutableList<FavoritePlayerSummary>
    {
        val favoritePlayerSummaries = mutableListOf<FavoritePlayerSummary>()

        DatabaseConnection.database.from(BiosTable)
            .select(BiosTable.playerId, BiosTable.firstName, BiosTable.lastName, BiosTable.currentPosition, BiosTable.currentTeam)
            .where{ (BiosTable.playerId inList favoritePlayerIds) }
            .forEach { playerRow ->
                val playerId    = playerRow[BiosTable.playerId          ] ?: ""
                val firstName   = playerRow[BiosTable.firstName         ] ?: ""
                val lastName    = playerRow[BiosTable.lastName          ] ?: ""
                val position    = playerRow[BiosTable.currentPosition   ] ?: ""
                val team        = playerRow[BiosTable.currentTeam       ] ?: ""

                favoritePlayerSummaries.add(FavoritePlayerSummary(playerId, firstName, lastName, position, team))
            }
        return favoritePlayerSummaries
    }
}