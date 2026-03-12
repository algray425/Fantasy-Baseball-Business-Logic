package com.advanced_baseball_stats.v2.repository

import com.advanced_baseball_stats.v2.repository.stats.DatabaseConnection
import com.advanced_baseball_stats.v2.repository.stats.tables.BiosTable

import org.ktorm.dsl.*

object PlayerBioSql
{
    fun isValidPlayer(playerId: String): Boolean
    {
        DatabaseConnection.database.from(BiosTable)
            .select(BiosTable.playerId)
            .where { BiosTable.playerId eq playerId }
            .forEach { player ->
                return true;
            }
        return false;
    }
}