package com.advanced_baseball_stats.v2.repository.stats.tables

import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar
import org.ktorm.schema.double

object SeasonStatsHittingTable : Table<Nothing>("SeasonStatsHitting")
{
    val key = varchar("key")
    val playerId = varchar("playerId")
    val season = int("season")
    val teams = varchar("teams")
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
    val groundedIntoDoublePlays = int("groundedIntoDoublePlays")
    val catcherInterference = int("catcherInterference")
    val battingAverage = double("battingAverage")
    val onBasePercentage = double("onBasePercentage")
    val sluggingPercentage = double("sluggingPercentage")
    val onBasePlusSlugging = double("onBasePlusSlugging")
    val babip = double("babip")
    val runsPerTob = double("runsPerTob")
    val rbisPerBip = double("rbisPerBip")
    val spd = double("spd")
    val groundBallPercentage = double("groundBallPercentage")
    val flyBallPercentage = double("flyBallPercentage")
    val lineDrivePercentage = double("lineDrivePercentage")
    val popUpPercentage = double("popUpPercentage")
    val hardHitPercentage = double("hardHitPercentage")
    val barrelPercentage = double("barrelPercentage")
}
