package com.advanced_baseball_stats.v2.repository.stats.tables

import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar
import org.ktorm.schema.double

object SeasonGradesTeamHittingTable : Table<Nothing>("SeasonGradesTeamHitting")
{
    val key = varchar("key")
    val team = varchar("team")
    val season = int("season")
    val percentileEra = double("percentileEra")
    val percentileWhip = double("percentileWhip")
    val percentileKsPerNine = double("percentileKsPerNine")
    val percentileQualityStarts = double("percentileQualityStarts")
    val percentileSavesAndHolds = double("percentileSavesAndHolds")
    val percentileOverall = double("percentileOverall")
    val percentileStartingPitchers = double("percentileStartingPitchers")
    val percentileReliefPitchers = double("percentileReliefPitchers")
}