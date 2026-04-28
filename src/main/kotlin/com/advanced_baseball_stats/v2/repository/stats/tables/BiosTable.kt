package com.advanced_baseball_stats.v2.repository.stats.tables

import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar
import org.ktorm.schema.double

object BiosTable : Table<Nothing>("Bios")
{
    val playerId = varchar("playerId")
    val firstName = varchar("firstName")
    val lastName = varchar("lastName")
    val nickName = varchar("nickName")
    val dob = int("dob")
    val debutYear = int("debutYear")
    val lastPlayedYear = int("lastPlayedYear")
    val batSide = varchar("batSide")
    val throwHand = varchar("throwHand")
    val height = varchar("height")
    val weight = double("weight")
    val currentTeam = varchar("currentTeam")
    val currentPosition = varchar("currentPosition")
    val currentStatus = varchar("currentStatus")
    val espnId = varchar("espnId")
    val fantraxId = varchar("fantraxId")
}