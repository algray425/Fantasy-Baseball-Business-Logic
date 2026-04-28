package com.advanced_baseball_stats.transformer

import com.advanced_baseball_stats.v2.model.fantasy.HolisiticFantasyLeague
import com.advanced_baseball_stats.v2.model.fantasy.HolisticFantasyRoster
import com.advanced_baseball_stats.v2.model.fantrax.roster.FanTraxLeague

class FanTraxToHolisticLeagueTransformer
{
    fun transform(fanTraxLeague: FanTraxLeague): HolisiticFantasyLeague
    {
        val holisticRosters = mutableListOf<HolisticFantasyRoster>()

        for (team in fanTraxLeague.rosters)
        {
            val players = mutableListOf<String>()

            for (player in team.value.players)
            {
                players.add(player.id)
            }

            holisticRosters.add(HolisticFantasyRoster(team.key, players))
        }

        return HolisiticFantasyLeague(holisticRosters)
    }
}