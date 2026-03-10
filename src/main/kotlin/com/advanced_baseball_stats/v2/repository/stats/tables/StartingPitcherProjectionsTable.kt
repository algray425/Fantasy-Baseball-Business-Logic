package com.advanced_baseball_stats.v2.repository.stats.tables

import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar
import org.ktorm.schema.double

object StartingPitcherProjectionsTable : Table<Nothing>("StartingPitcherProjections")
{
    val playerId = varchar("playerId")
    val qualityStarts = int("qualityStarts")
    val era = double("era")
    val whip = double("whip")
    val ksPerNine = double("ksPerNine")
    val grade = double("grade")
    val percentileQualityStarts = double("percentileQualityStarts")
    val percentileEra = double("percentileEra")
    val percentileWhip = double("percentileWhip")
    val percentileKsPerNine = double("percentileKsPerNine")
    val percentileGrade = double("percentileGrade")
}