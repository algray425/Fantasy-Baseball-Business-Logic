package com.advanced_baseball_stats.v2.repository.stats

import com.advanced_baseball_stats.v2.repository.stats.tables.SeasonGradesTeamPitchingTable

import org.ktorm.dsl.from
import org.ktorm.dsl.select
import org.ktorm.dsl.where
import org.ktorm.dsl.eq
import org.ktorm.dsl.and
import org.ktorm.dsl.forEachIndexed

object TeamPitchingSql
{
    fun getPitchingGradesByTeam(team: String, season: Int): Double?
    {
        DatabaseConnection.database.from(SeasonGradesTeamPitchingTable)
            .select(SeasonGradesTeamPitchingTable.percentileOverall)
            .where{(SeasonGradesTeamPitchingTable.team eq team) and (SeasonGradesTeamPitchingTable.season eq season)}
            .forEachIndexed { index,teamRow ->
                if (index == 0)
                {
                    return teamRow[SeasonGradesTeamPitchingTable.percentileOverall]
                }
            }
        return null
    }
}