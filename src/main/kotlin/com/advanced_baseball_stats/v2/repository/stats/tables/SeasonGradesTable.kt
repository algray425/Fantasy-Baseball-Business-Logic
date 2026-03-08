package com.advanced_baseball_stats.v2.repository.stats.tables

import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar
import org.ktorm.schema.double

object SeasonGradesTable : Table<Nothing>("SeasonGrades")
{
    val key = varchar("key")
    val playerId = varchar("playerId")
    val season = int("season")
    val percentileHomeRuns = double("percentileHomeRuns")
    val qualifiedPercentileHomeRuns = double("qualifiedPercentileHomeRuns")
    val percentileRbis = double("percentileRbis")
    val qualifiedPercentileRbis = double("qualifiedPercentileRbis")
    val percentileStolenBases = double("percentileStolenBases")
    val qualifiedPercentileStolenBases = double("qualifiedPercentileStolenBases")
    val percentileRuns = double("percentileRuns")
    val qualifiedPercentileRuns = double("qualifiedPercentileRuns")
    val percentileOnBasePercentage = double("percentileOnBasePercentage")
    val qualifiedPercentileOnBasePercentage = double("qualifiedPercentileOnBasePercentage")
    val percentileOverall = double("percentileOverall")
    val percentileOverallQualified = double("percentileOverallQualified")
    val overallGrade = double("overallGrade")
    val overallGradeQualified = double("overallGradeQualified")
    val qualified = int("qualified")
}