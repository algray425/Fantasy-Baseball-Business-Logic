package com.advanced_baseball_stats.model.batting

import com.advanced_baseball_stats.model.game.Game
import com.advanced_baseball_stats.model.common.Team
import kotlinx.serialization.Serializable

@Serializable
class HolisticBattingStatistic(
        val statName: BattingStat
    ,   val game: Game
    ,   val date: String
    ,   val num: Double
    ,   val playerTeam: Team = Team.UNKNOWN
)
{
}