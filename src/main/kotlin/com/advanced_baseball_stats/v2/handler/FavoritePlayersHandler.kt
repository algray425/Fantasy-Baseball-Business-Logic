package com.advanced_baseball_stats.v2.handler

import com.advanced_baseball_stats.v2.model.batters.FavoritePlayers.FavoritePlayerInfo
import com.advanced_baseball_stats.v2.model.users.FavoritePlayerSummary
import com.advanced_baseball_stats.v2.repository.users.FavoritePlayersSql

class FavoritePlayersHandler
{
    fun getFavoritePlayers(userId: String): MutableList<FavoritePlayerSummary>
    {
        val favoritePlayerIds =  FavoritePlayersSql.getFavoritePlayersIds(userId)

        return FavoritePlayersSql.getFavoritePlayerSummaries(favoritePlayerIds)
    }

    fun addFavoritePlayer(favoritePlayerInfo: FavoritePlayerInfo)
    {
        FavoritePlayersSql.addFavoritePlayer(favoritePlayerInfo.userId, favoritePlayerInfo.playerId)
    }

    fun removeFavoritePlayer(userId: String, playerId: String)
    {
        FavoritePlayersSql.removeFavoritePlayer(userId, playerId)
    }
}