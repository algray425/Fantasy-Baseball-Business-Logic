package com.advanced_baseball_stats.v2.repository.stats.tables

import org.ktorm.schema.Table
import org.ktorm.schema.double
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object PerGameStatsPitchingTable : Table<Nothing>("PerGameStatsPitching")
{
    val key = varchar("key")
    val gameId = varchar("gameId")
    val pitcherId = varchar("pitcherId")
    val teamId = varchar("teamId")
    val sequence = int("sequence")
    val ipOuts = int("ipOuts")
    val battersFaced = int("battersFaced")
    val hits = int("hits")
    val singles = int("singles")
    val doubles = int("doubles")
    val triples = int("triples")
    val homeRuns = int("homeRuns")
    val runs = int("runs")
    val earnedRuns = int("earnedRuns")
    val walks = int("walks")
    val intentionalWalks = int("intentionalWalks")
    val strikeOuts = int("strikeOuts")
    val hitByPitch = int("hitByPitch")
    val wildPitches = int("wildPitches")
    val balks = int("balks")
    val sacrificeHits = int("sacrificeHits")
    val sacrificeFlies = int("sacrificeFlies")
    val stolenBases = int("stolenBases")
    val caughtStealing = int("caughtStealing")
    val passedBalls = int("passedBalls")
    val gameStarted = int("gameStarted")
    val gameFinished = int("gameFinished")
    val completeGame = int("completeGame")
    val hold = int("hold")
    val era = double("era")
    val whip = double("whip")
    val ksPerNine = double("ksPerNine")
    val qualityStart = int("qualityStart")
}