package com.advanced_baseball_stats.v2.repository.stats.tables

import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar
import org.ktorm.schema.double

object SeasonGradesReliefPitchersTable : Table<Nothing>("SeasonGradesReliefPitchers")
{
    val key = varchar("key")
    val playerId = varchar("playerId")
    val season = int("season")
    val percentileEra = double("percentileEra")
    val qualifiedPercentileEra = double("qualifiedPercentileEra")
    val percentileWhip = double("percentileWhip")
    val qualifiedPercentileWhip = double("qualifiedPercentileWhip")
    val percentileKsPerNine = double("percentileKsPerNine")
    val qualifiedPercentileKsPerNine = double("qualifiedPercentileKsPerNine")
    val percentileSavesAndHolds = double("percentileSavesAndHolds")
    val qualifiedPercentileSavesAndHolds = double("qualifiedPercentileSavesAndHolds")
    val percentileOverall = double("percentileOverall")
    val percentileOverallQualified = double("percentileOverallQualified")
    val overallGrade = double("overallGrade")
    val overallGradeQualified = double("overallGradeQualified")
    val qualified = int("qualified")
}