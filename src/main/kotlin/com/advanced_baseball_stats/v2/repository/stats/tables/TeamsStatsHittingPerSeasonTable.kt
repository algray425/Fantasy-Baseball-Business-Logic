package com.advanced_baseball_stats.v2.repository.stats.tables

import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar
import org.ktorm.schema.double

object TeamsStatsHittingPerSeasonTable : Table<Nothing>("TeamsStatsHittingPerSeason")
{
    val key = varchar("key")
    val team = varchar("team")
    val season = int("season")
    val plateAppearances = int("plateAppearances")
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
    val battingAverage = double("battingAverage")
    val onBasePercentage = double("onBasePercentage")
    val sluggingPercentage = double("sluggingPercentage")
    val onBasePlusSlugging = double("onBasePlusSlugging")
    val eraAgainst = double("eraAgainst")
    val whipAgainst = double("whipAgainst")
    val ksPerNineAgainst = double("ksPerNineAgainst")
    val qualityStartsAgainst = int("qualityStartsAgainst")
    val savesAgainst = int("savesAgainst")
    val holdsAgainst = int("holdsAgainst")
}