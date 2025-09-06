package com.advanced_baseball_stats.handler.player

import com.advanced_baseball_stats.cache.RedisCache
import com.advanced_baseball_stats.model.player.Player
import com.advanced_baseball_stats.model.player.SearchablePlayer
import com.advanced_baseball_stats.repository.PlayerBioSql
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.json.*

object PlayerStatHandler
{
    fun getPlayersByName(name: String, uri: String) : MutableSet<SearchablePlayer>
    {
        val cachedSearchablePlayer = RedisCache.retrieveFromCache(uri)

        if (cachedSearchablePlayer != null)
        {
            val searchablePlayers: MutableSet<SearchablePlayer> = Json.decodeFromString(SetSerializer(SearchablePlayer.serializer()), cachedSearchablePlayer).toMutableSet()

            println("Got searchable player from cache!")

            return searchablePlayers
        }
        else
        {
            val searchablePlayers = PlayerBioSql.getPlayers(name)

            println("Got searchable player from database!")

            val serializedSearchablePlayers = Json.encodeToString(SetSerializer(SearchablePlayer.serializer()), searchablePlayers)

            RedisCache.addToCache(uri, serializedSearchablePlayers, 60)

            return searchablePlayers
        }
    }

    fun getPlayerFromId(id: String, uri: String) : Player
    {
        val cachedPlayer = RedisCache.retrieveFromCache(uri)

        if (cachedPlayer != null)
        {
            val player: Player = Json.decodeFromString(cachedPlayer)

            println("Got player from cache!")

            return player
        }
        else
        {
            val player = PlayerBioSql.getPlayer(id)

            RedisCache.addToCache(uri, Json.encodeToString(player), 60)

            println("Got player from database!")

            return player
        }
    }
}