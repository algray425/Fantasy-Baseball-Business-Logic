package com.advanced_baseball_stats.v2.repository.stats.tables

import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar
import org.ktorm.schema.double

object TeamsStatsPitchingPerSeasonTable : Table<Nothing>("TeamsStatsPitchingPerSeason")
{
    val key = varchar("key")
    val team = varchar("team")
    val season = int("season")
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
    val sacHits = int("sacHits")
    val sacFlies = int("sacFlies")
    val stolenBases = int("stolenBases")
    val caughtStealing = int("caughtStealing")
    val passedBalls = int("passedBalls")
    val era = double("era")
    val whip = double("whip")
    val ksPerNine = double("ksPerNine")
    val rbisAgainst = int("rbisAgainst")
    val onBasePercentageAgainst = double("onBasePercentageAgainst")
}