package com.advanced_baseball_stats.v2.repository.stats.tables

import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar
import org.ktorm.schema.double

object PerGameStatsHittingTable : Table<Nothing>("PerGameStatsHitting")
{
    val key = varchar("key")
    val gameId = varchar("gameId")
    val playerId = varchar("playerId")
    val teamId = varchar("teamId")
    val lineupPosition = int("lineupPosition")
    val sequence = int("sequence")
    val plateAppearences = int("plateAppearences")
    val atBats = int("atBats")
    val runs = int("runs")
    val hits = int("hits")
    val singles = int("singles")
    val doubles = int("doubles")
    val triples = int("triples")
    val homeRuns = int("homeRuns")
    val rbis = int("rbis")
    val sacHits = int("sacHits")
    val sacFlies = int("sacFlies")
    val hitByPitch = int("hitByPitch")
    val walks = int("walks")
    val intentionalWalks = int("intentionalWalks")
    val strikeOuts = int("strikeOuts")
    val stolenBases = int("stolenBases")
    val caughtStealing = int("caughtStealing")
    val groundIntoDoublePlays = int("groundIntoDoublePlays")
    val catcherInterference = int("catcherInterference")
    val designatedHitter = int("designatedHitter")
    val pinchHitter = int("pinchHitter")
    val pinchRunner = int("pinchRunner")
    val battingAverage = double("battingAverage")
    val onBasePercentage = double("onBasePercentage")
    val sluggingPercentage = double("sluggingPercentage")
    val onBasePlusSlugging = double("onBasePlusSlugging")
}
