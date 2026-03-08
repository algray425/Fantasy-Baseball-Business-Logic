package com.advanced_baseball_stats.v2.repository.stats.tables

import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar
import org.ktorm.schema.double

object SeasonGradesTeamPitchingTable : Table<Nothing>("SeasonGradesTeamPitching")
{
    val key = varchar("key")
    val team = varchar("team")
    val season = int("season")
    val percentileRuns = double("percentileRuns")
    val percentileHomeRuns = double("percentileHomeRuns")
    val percentileRbis = double("percentileRbis")
    val percentileStolenBases = double("percentileStolenBases")
    val percentileOnBasePercentage = double("percentileOnBasePercentage")
    val percentileOverall = double("percentileOverall")
}