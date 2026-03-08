package com.advanced_baseball_stats.v2.repository.stats.tables

import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar
import org.ktorm.schema.double

object HitterProjectionsTable : Table<Nothing>("HitterProjections")
{
    val playerId = varchar("playerId")
    val runs = int("runs")
    val homeRuns = int("homeRuns")
    val rbis = int("rbis")
    val stolenBases = int("stolenBases")
    val onBasePercentage = double("onBasePercentage")
    val overallPercentileRuns = double("overallPercentileRuns")
    val overallPercentileHomeRuns = double("overallPercentileHomeRuns")
    val overallPercentileRbis = double("overallPercentileRbis")
    val overallPercentileStolenBases = double("overallPercentileStolenBases")
    val overallPercentileObp = double("overallPercentileObp")
    val overallGrade = double("overallGrade")
    val overallGradePercentile = double("overallGradePercentile")
    val qualifiedPercentileRuns = double("qualifiedPercentileRuns")
    val qualifiedPercentileHomeRuns = double("qualifiedPercentileHomeRuns")
    val qualifiedPercentileRbis = double("qualifiedPercentileRbis")
    val qualifiedPercentileStolenBases = double("qualifiedPercentileStolenBases")
    val qualifiedPercentileObp = double("qualifiedPercentileObp")
    val qualifiedGrade = double("qualifiedGrade")
    val qualifiedGradePercentile = double("qualifiedGradePercentile")
    val qualified = int("qualified")
}