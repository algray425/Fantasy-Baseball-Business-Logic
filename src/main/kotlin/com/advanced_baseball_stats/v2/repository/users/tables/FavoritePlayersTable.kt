package com.advanced_baseball_stats.v2.repository.users.tables

import org.ktorm.schema.Table
import org.ktorm.schema.varchar

object FavoritePlayersTable: Table<Nothing>("FavoritePlayers")
{
    val key = varchar("key")
    val playerId = varchar("playerId")
    val userId = varchar("userId")
}