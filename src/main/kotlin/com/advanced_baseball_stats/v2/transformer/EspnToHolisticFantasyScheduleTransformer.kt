package com.advanced_baseball_stats.v2.transformer

import com.advanced_baseball_stats.v2.model.espn.matchup.EspnSchedule
import com.advanced_baseball_stats.v2.model.fantasy.FantasyMatchup
import com.advanced_baseball_stats.v2.model.fantasy.FantasyTeamMatchupSummary

class EspnToHolisticFantasyScheduleTransformer
{
    fun transform(espnSchedule: EspnSchedule, teamId: Int, weekNumbers: Set<Int>): MutableList<FantasyMatchup>
    {
        val fantasyMatchups = mutableListOf<FantasyMatchup>()

        for (matchup in espnSchedule.schedule)
        {
            if (weekNumbers.contains(matchup.matchupPeriodId) && (matchup.awayTeam.teamId == teamId || matchup.homeTeam.teamId == teamId))
            {
                val homeTeamId              = matchup.homeTeam.teamId
                val homeTeamWins            = matchup.homeTeam.cumulativeScore.wins
                val homeTeamLosses          = matchup.homeTeam.cumulativeScore.losses
                val homeTeamTies            = matchup.homeTeam.cumulativeScore.ties

                val homeTeamRuns            = matchup.homeTeam.cumulativeScore.scoreByStat?.runsStat?.score ?: 0
                val homeTeamHomeRuns        = matchup.homeTeam.cumulativeScore.scoreByStat?.homeRunsStat?.score ?: 0
                val homeTeamRbis            = matchup.homeTeam.cumulativeScore.scoreByStat?.rbisStat?.score ?: 0
                val homeTeamStolenBases     = matchup.homeTeam.cumulativeScore.scoreByStat?.stolenBasesStat?.score ?: 0
                val homeTeamObp             = matchup.homeTeam.cumulativeScore.scoreByStat?.obpStat?.score ?: 0.0
                val homeTeamQualityStarts   = matchup.homeTeam.cumulativeScore.scoreByStat?.qualityStartsStat?.score ?: 0
                val homeTeamSavesPlusHolds  = matchup.homeTeam.cumulativeScore.scoreByStat?.savesAndHoldsStat?.score ?: 0
                val homeTeamEra             = matchup.homeTeam.cumulativeScore.scoreByStat?.eraStat?.score ?: 0.0
                val homeTeamWhip            = matchup.homeTeam.cumulativeScore.scoreByStat?.whipStat?.score ?: 0.0
                val homeTeamKsPerNine       = matchup.homeTeam.cumulativeScore.scoreByStat?.ksPerNineStat?.score ?: 0.0

                val homeTeamSummary = FantasyTeamMatchupSummary(homeTeamId, homeTeamWins, homeTeamLosses, homeTeamTies, homeTeamRuns.toInt(), homeTeamHomeRuns.toInt(),
                    homeTeamRbis.toInt(), homeTeamStolenBases.toInt(), homeTeamObp, homeTeamQualityStarts.toInt(), homeTeamSavesPlusHolds.toInt(), homeTeamEra,
                    homeTeamWhip, homeTeamKsPerNine)

                val awayTeamId              = matchup.awayTeam.teamId
                val awayTeamWins            = matchup.awayTeam.cumulativeScore.wins
                val awayTeamLosses          = matchup.awayTeam.cumulativeScore.losses
                val awayTeamTies            = matchup.awayTeam.cumulativeScore.ties

                val awayTeamRuns            = matchup.awayTeam.cumulativeScore.scoreByStat?.runsStat?.score ?: 0
                val awayTeamHomeRuns        = matchup.awayTeam.cumulativeScore.scoreByStat?.homeRunsStat?.score ?: 0
                val awayTeamRbis            = matchup.awayTeam.cumulativeScore.scoreByStat?.rbisStat?.score ?: 0
                val awayTeamStolenBases     = matchup.awayTeam.cumulativeScore.scoreByStat?.stolenBasesStat?.score ?: 0
                val awayTeamObp             = matchup.awayTeam.cumulativeScore.scoreByStat?.obpStat?.score ?: 0.0
                val awayTeamQualityStarts   = matchup.awayTeam.cumulativeScore.scoreByStat?.qualityStartsStat?.score ?: 0
                val awayTeamSavesPlusHolds  = matchup.awayTeam.cumulativeScore.scoreByStat?.savesAndHoldsStat?.score ?: 0
                val awayTeamEra             = matchup.awayTeam.cumulativeScore.scoreByStat?.eraStat?.score ?: 0.0
                val awayTeamWhip            = matchup.awayTeam.cumulativeScore.scoreByStat?.whipStat?.score ?: 0.0
                val awayTeamKsPerNine       = matchup.awayTeam.cumulativeScore.scoreByStat?.ksPerNineStat?.score ?: 0.0

                val awayTeamSummary = FantasyTeamMatchupSummary(awayTeamId, awayTeamWins, awayTeamLosses, awayTeamTies, awayTeamRuns.toInt(), awayTeamHomeRuns.toInt(),
                    awayTeamRbis.toInt(), awayTeamStolenBases.toInt(), awayTeamObp, awayTeamQualityStarts.toInt(), awayTeamSavesPlusHolds.toInt(), awayTeamEra,
                    awayTeamWhip, awayTeamKsPerNine)

                fantasyMatchups.add(FantasyMatchup(homeTeamSummary, awayTeamSummary))
            }
        }
        return fantasyMatchups
    }
}