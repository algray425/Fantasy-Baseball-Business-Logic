package com.advanced_baseball_stats.repository

import com.advanced_baseball_stats.model.common.Team
import com.advanced_baseball_stats.model.database.TeamPitching
import org.ktorm.dsl.*
import com.advanced_baseball_stats.repository.tables.*
import org.ktorm.schema.*
import java.math.RoundingMode

object TeamStatsSql {
    private const val ERA_COLUMN_NAME           = "p_era"
    private const val WHIP_COLUMN_NAME          = "p_whip"
    private const val HOME_RUNS_COLUMN_NAME     = "homeruns"
    private const val STRIKE_OUTS_COLUMN_NAME   = "strikeouts"

    private val eraColumn           = ((sum(PitcherTable.p_er.cast(SqlType.of<Double>()!!)) times 9.0) div (sum(PitcherTable.p_ipouts.cast(SqlType.of<Double>()!!)) div 3.0)).aliased(ERA_COLUMN_NAME)
    private val whipColumn          = (((sum(PitcherTable.p_w.cast(SqlType.of<Double>()!!))) + (sum(PitcherTable.p_h.cast(SqlType.of<Double>()!!)))) div (sum(PitcherTable.p_ipouts.cast(SqlType.of<Double>()!!)) div 3.0)).aliased(WHIP_COLUMN_NAME)
    private val homeRunsColumn      = (sum(PitcherTable.p_hr)).aliased(HOME_RUNS_COLUMN_NAME)
    private val strikeOutsColumn    = (sum(PitcherTable.p_k)).aliased(STRIKE_OUTS_COLUMN_NAME)

    fun getTeamPitching(team: String, startDate: String, endDate: String): TeamPitching?
    {
        val dateRanges: ClosedRange<String> = startDate..endDate

        var teamPitching: TeamPitching? = null

        DatabaseConnection.database.from(PitcherTable)
            .select(eraColumn, whipColumn, homeRunsColumn, strikeOutsColumn)
            .where { (PitcherTable.date between dateRanges) and (PitcherTable.team eq team) }
            .orderBy(PitcherTable.date.asc())
            .limit(1)
            .forEach { pitcher ->
                val teamEra         = pitcher[eraColumn         ]!!
                val teamWhip        = pitcher[whipColumn        ]!!
                val teamHomeRuns    = pitcher[homeRunsColumn    ]!!
                val teamStrikeouts  = pitcher[strikeOutsColumn  ]!!

                val roundedTeamEra  = teamEra .toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
                val roundedTeamWhip = teamWhip.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()

                teamPitching = TeamPitching(team, roundedTeamEra, roundedTeamWhip, teamHomeRuns, teamStrikeouts, 0)
            }
            return teamPitching
    }
}