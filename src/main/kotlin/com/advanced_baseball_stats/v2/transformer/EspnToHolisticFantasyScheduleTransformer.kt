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

                val homeTeamRuns            = matchup.homeTeam.cumulativeScore.scoreByStat.runsStat.score
                val homeTeamHomeRuns        = matchup.homeTeam.cumulativeScore.scoreByStat.homeRunsStat.score
                val homeTeamRbis            = matchup.homeTeam.cumulativeScore.scoreByStat.rbisStat.score
                val homeTeamStolenBases     = matchup.homeTeam.cumulativeScore.scoreByStat.stolenBasesStat.score
                val homeTeamObp             = matchup.homeTeam.cumulativeScore.scoreByStat.obpStat.score
                val homeTeamQualityStarts   = matchup.homeTeam.cumulativeScore.scoreByStat.qualityStartsStat.score
                val homeTeamSavesPlusHolds  = matchup.homeTeam.cumulativeScore.scoreByStat.savesAndHoldsStat.score
                val homeTeamEra             = matchup.homeTeam.cumulativeScore.scoreByStat.eraStat.score
                val homeTeamWhip            = matchup.homeTeam.cumulativeScore.scoreByStat.whipStat.score
                val homeTeamKsPerNine       = matchup.homeTeam.cumulativeScore.scoreByStat.ksPerNineStat.score

                val homeTeamSummary = FantasyTeamMatchupSummary(homeTeamId, homeTeamWins, homeTeamLosses, homeTeamTies, homeTeamRuns.toInt(), homeTeamHomeRuns.toInt(),
                    homeTeamRbis.toInt(), homeTeamStolenBases.toInt(), homeTeamObp, homeTeamQualityStarts.toInt(), homeTeamSavesPlusHolds.toInt(), homeTeamEra,
                    homeTeamWhip, homeTeamKsPerNine)

                val awayTeamId              = matchup.awayTeam.teamId
                val awayTeamWins            = matchup.awayTeam.cumulativeScore.wins
                val awayTeamLosses          = matchup.awayTeam.cumulativeScore.losses
                val awayTeamTies            = matchup.awayTeam.cumulativeScore.ties

                val awayTeamRuns            = matchup.awayTeam.cumulativeScore.scoreByStat.runsStat.score
                val awayTeamHomeRuns        = matchup.awayTeam.cumulativeScore.scoreByStat.homeRunsStat.score
                val awayTeamRbis            = matchup.awayTeam.cumulativeScore.scoreByStat.rbisStat.score
                val awayTeamStolenBases     = matchup.awayTeam.cumulativeScore.scoreByStat.stolenBasesStat.score
                val awayTeamObp             = matchup.awayTeam.cumulativeScore.scoreByStat.obpStat.score
                val awayTeamQualityStarts   = matchup.awayTeam.cumulativeScore.scoreByStat.qualityStartsStat.score
                val awayTeamSavesPlusHolds  = matchup.awayTeam.cumulativeScore.scoreByStat.savesAndHoldsStat.score
                val awayTeamEra             = matchup.awayTeam.cumulativeScore.scoreByStat.eraStat.score
                val awayTeamWhip            = matchup.awayTeam.cumulativeScore.scoreByStat.whipStat.score
                val awayTeamKsPerNine       = matchup.awayTeam.cumulativeScore.scoreByStat.ksPerNineStat.score

                val awayTeamSummary = FantasyTeamMatchupSummary(awayTeamId, awayTeamWins, awayTeamLosses, awayTeamTies, awayTeamRuns.toInt(), awayTeamHomeRuns.toInt(),
                    awayTeamRbis.toInt(), awayTeamStolenBases.toInt(), awayTeamObp, awayTeamQualityStarts.toInt(), awayTeamSavesPlusHolds.toInt(), awayTeamEra,
                    awayTeamWhip, awayTeamKsPerNine)

                fantasyMatchups.add(FantasyMatchup(homeTeamSummary, awayTeamSummary))
            }
        }
        return fantasyMatchups
    }
}