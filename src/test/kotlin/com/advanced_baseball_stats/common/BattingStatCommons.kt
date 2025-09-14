package com.advanced_baseball_stats.common

import com.advanced_baseball_stats.model.batting.BattingGame
import com.advanced_baseball_stats.model.batting.BattingGameStat
import com.advanced_baseball_stats.model.batting.BattingStat
import com.advanced_baseball_stats.model.batting.HolisticBattingStatList
import com.advanced_baseball_stats.model.common.Team
import com.advanced_baseball_stats.model.game.*

object BattingStatCommons
{
    val ID                          = "ralec001"
    val START_DATE                  = "2025-04-01"
    val END_DATE                    = "2025-04-02"
    val EMPTY_END_DATE              = ""
    val CURRENT_END_DATE            = "2025-04-03"
    val EMPTY_PITCHER_ID            = ""
    val EMPTY_PITCHER_HANDEDNESS    = ""
    val HIT_STAT                    = BattingStat.HIT
    val STAT_LIST                   = mutableListOf(HIT_STAT)

    val FIRST_GID       = "GID1"
    val SECOND_GID      = "GID2"
    val TEMP            = 69
    val WIND_SPEED      = 420
    val WIND_DIRECTION  = WindDirection.FROM_RIGHT_FIELD
    val CONDITION       = Condition.OVERCAST
    val PRECIPITATION   = Precipitation.DRIZZLE
    val TIME_OF_DAY     = TimeOfDay.NIGHT
    val HOME_TEAM       = Team.SEA
    val AWAY_TEAM       = Team.SDN

    val STAT_NAME_HIT   = BattingStat.HIT
    val FIRST_HIT_NUM   = 2.0
    val SECOND_HIT_NUM  = 2.0

    val FIRST_GAME                                      = Game(FIRST_GID, TEMP, WIND_SPEED, WIND_DIRECTION, CONDITION, PRECIPITATION, TIME_OF_DAY, HOME_TEAM, AWAY_TEAM)
    val SECOND_GAME                                     = Game(SECOND_GID, TEMP, WIND_SPEED, WIND_DIRECTION, CONDITION, PRECIPITATION, TIME_OF_DAY, HOME_TEAM, AWAY_TEAM)
    val FIRST_BATTING_GAME_STAT_HIT                     = BattingGameStat(STAT_NAME_HIT, FIRST_HIT_NUM)
    val SECOND_BATTING_GAME_STAT_HIT                    = BattingGameStat(STAT_NAME_HIT, SECOND_HIT_NUM)
    val FIRST_BATTING_GAME_HIT                          = BattingGame(FIRST_GAME, mutableListOf(FIRST_BATTING_GAME_STAT_HIT))
    val SECOND_BATTING_GAME_HIT                         = BattingGame(SECOND_GAME, mutableListOf(SECOND_BATTING_GAME_STAT_HIT))
    val HOLISTIC_BATTING_STAT_LIST_HIT                  = HolisticBattingStatList(ID, listOf(FIRST_BATTING_GAME_HIT))
    val HOLISTIC_BATTING_STAT_LIST_HIT_EMPTY_END_DATE   = HolisticBattingStatList(ID, listOf(FIRST_BATTING_GAME_HIT, SECOND_BATTING_GAME_HIT))
}