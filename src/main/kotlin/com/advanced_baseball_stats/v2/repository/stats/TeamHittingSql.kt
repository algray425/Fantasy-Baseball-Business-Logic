package com.advanced_baseball_stats.v2.repository.stats

import com.advanced_baseball_stats.v2.repository.stats.tables.SeasonGradesTeamHittingTable

import org.ktorm.dsl.from
import org.ktorm.dsl.select
import org.ktorm.dsl.where
import org.ktorm.dsl.eq
import org.ktorm.dsl.and
import org.ktorm.dsl.forEachIndexed

object TeamHittingSql
{
    fun getHittingGradesByTeam(team: String, season: Int): Double?
    {
        DatabaseConnection.database.from(SeasonGradesTeamHittingTable)
            .select(SeasonGradesTeamHittingTable.percentileOverall)
            .where{(SeasonGradesTeamHittingTable.team eq team) and (SeasonGradesTeamHittingTable.season eq season)}
            .forEachIndexed{ index,teamRow ->
                if (index == 0)
                {
                    return teamRow[SeasonGradesTeamHittingTable.percentileOverall]
                }
            }
        return null
    }
}