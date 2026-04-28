package com.advanced_baseball_stats.transformer

import com.advanced_baseball_stats.v2.model.espn.roster.EspnRosters
import com.advanced_baseball_stats.v2.model.fantasy.HolisiticFantasyLeague
import com.advanced_baseball_stats.v2.model.fantasy.HolisticFantasyRoster

class EspnToHolisticLeagueTransformer
{
    fun transform(rosters: EspnRosters): HolisiticFantasyLeague
    {
        val holisticRosters = mutableListOf<HolisticFantasyRoster>()

        for (team in rosters.teams)
        {
            val players = mutableListOf<String>()

            for (player in team.roster.playerEntry)
            {
                players.add(player.espnId.toString())
            }

            holisticRosters.add(HolisticFantasyRoster(team.id.toString(), players));
        }

        return HolisiticFantasyLeague(holisticRosters)
    }
}