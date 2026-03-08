package com.advanced_baseball_stats.v2.repository.users.tables

import org.ktorm.schema.Table
import org.ktorm.schema.varchar

object FantasyTeamsTable : Table<Nothing>("FantasyTeams")
{
    val key = varchar("key")
    val leagueType = varchar("leagueType")
    val teamId = varchar("teamId")
    val userId = varchar("userId")
    val teamName = varchar("teamName")
    val leagueName = varchar("leagueName")
    val leagueId = varchar("leagueId")
}