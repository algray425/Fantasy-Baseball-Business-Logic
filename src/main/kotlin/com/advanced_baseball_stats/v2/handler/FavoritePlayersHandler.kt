package com.advanced_baseball_stats.v2.handler

import com.advanced_baseball_stats.v2.exception.InvalidPlayerIdException
import com.advanced_baseball_stats.v2.exception.InvalidUserException
import com.advanced_baseball_stats.v2.model.batters.FavoritePlayers.FavoritePlayerInfo
import com.advanced_baseball_stats.v2.model.users.FavoritePlayerSummary
import com.advanced_baseball_stats.v2.repository.PlayerBioSql
import com.advanced_baseball_stats.v2.repository.users.FavoritePlayersSql
import com.advanced_baseball_stats.v2.repository.users.UserSql

class FavoritePlayersHandler
{
    fun getFavoritePlayers(userId: String): MutableList<FavoritePlayerSummary>
    {
        val favoritePlayerIds =  FavoritePlayersSql.getFavoritePlayersIds(userId)

        return FavoritePlayersSql.getFavoritePlayerSummaries(favoritePlayerIds)
    }

    fun addFavoritePlayer(favoritePlayerInfo: FavoritePlayerInfo)
    {
        val isValidUser = UserSql.isValidUser(favoritePlayerInfo.userId)

        if (!isValidUser)
        {
            throw InvalidUserException("UserId is invalid!")
        }

        val isValidPlayer = PlayerBioSql.isValidPlayer(favoritePlayerInfo.playerId)

        if (!isValidPlayer)
        {
            throw InvalidPlayerIdException("PlayerId is invalid!")
        }

        FavoritePlayersSql.addFavoritePlayer(favoritePlayerInfo.userId, favoritePlayerInfo.playerId)
    }

    fun removeFavoritePlayer(userId: String, playerId: String)
    {
        FavoritePlayersSql.removeFavoritePlayer(userId, playerId)
    }
}