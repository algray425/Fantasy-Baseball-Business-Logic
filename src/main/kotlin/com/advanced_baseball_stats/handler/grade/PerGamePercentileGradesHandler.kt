package com.advanced_baseball_stats.handler.grade

import com.advanced_baseball_stats.model.batting.BattingStat
import com.advanced_baseball_stats.model.grades.HolisticGrade
import com.advanced_baseball_stats.model.grades.PitchingGradeStat
import com.advanced_baseball_stats.repository.PlayerGradeSql

class PerGamePercentileGradesHandler : PercentileGradesHandler
{
    override fun getGrades(stat: BattingStat, percentileStart: Float, weekNumber: Int, season: Int, showAvailable: Boolean): MutableList<HolisticGrade>
    {
        return if (showAvailable)
        {
            PlayerGradeSql.getPerGamePercentileGradesWithAvailablePlayers(stat.toString(), percentileStart, weekNumber, season, true)
        } else
        {
            PlayerGradeSql.getPerGamePercentileBattingGrades(stat, percentileStart, weekNumber, season)
        }
    }

    /**
     * TODO:            UPDATE DATABASE LAYER TO RETURN DATABASE SPECIFIC MODEL, USE DATA TO PULL NEXT MATCHUP AND GRADES FOR TEAM, THEN TRANSFORM ALL DATA INTO
     * TODO (CONTINUED) HOLISTIC GRADE MODEL (SHOULD PROBABLY DO THIS FOR ALL DATABASE REQUESTS, THEN DECIDE WHETHER TO CACHE AT THE DATABASE LEVEL OR THE BUSINESS MODEL LEVEL)
    **/
    override fun getGrades(stat: PitchingGradeStat, percentileStart: Float, weekNumber: Int, season: Int, showAvailable: Boolean): MutableList<HolisticGrade>
    {
        return if (showAvailable)
        {
            PlayerGradeSql.getPerGamePercentileGradesWithAvailablePlayers(stat.toString(), percentileStart, weekNumber, season, false)
        } else
        {
            PlayerGradeSql.getPerGamePercentilePitchingGrades(stat, percentileStart, weekNumber, season)
        }
    }
}