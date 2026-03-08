package com.advanced_baseball_stats.v2.repository.stats.tables

import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object GamesTable : Table<Nothing>("Games")
{
    val gameId = varchar("gameId")
    val visTeam = varchar("visTeam")
    val homeTeam = varchar("homeTeam")
    val site = varchar("site")
    val date = varchar("date")
    val number = int("number")
    val startTime = int("startTime")
    val dayNight = varchar("dayNight")
    val innings = int("innings")
    val timeOfGame = int("timeOfGame")
    val attendance = int("attendance")
    val precipitation = varchar("precipitation")
    val sky = varchar("sky")
    val temp = int("temp")
    val windDirection = varchar("windDirection")
    val windSpeed = int("windSpeed")
    val suspended = int("suspended")
    val winningPitcher = varchar("winningPitcher")
    val losingPitcher = varchar("losingPitcher")
    val savingPitcher = varchar("savingPitcher")
    val gameType = varchar("gameType")
    val visTeamRuns = int("visTeamRuns")
    val homeTeamRuns = int("homeTeamRuns")
    val winningTeam = varchar("winningTeam")
    val losingTeam = varchar("losingTeam")
    val season = int("season")
}