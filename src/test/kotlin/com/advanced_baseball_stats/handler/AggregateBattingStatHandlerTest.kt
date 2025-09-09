package com.advanced_baseball_stats.handler

import com.advanced_baseball_stats.handler.batting.AggregateBattingStatHandler
import com.advanced_baseball_stats.model.batting.BattingGame
import com.advanced_baseball_stats.model.batting.BattingGameStat
import com.advanced_baseball_stats.model.batting.BattingStat
import com.advanced_baseball_stats.model.batting.HolisticBattingStatList
import com.advanced_baseball_stats.model.common.Team
import com.advanced_baseball_stats.model.game.*
import com.advanced_baseball_stats.repository.PlayerBattingSql
import com.advanced_baseball_stats.utility.converter.date.DateHelper
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlin.test.assertEquals

import org.junit.After
import org.junit.Before
import org.junit.Test

class AggregateBattingStatHandlerTest
{
    private val helper = AggregateBattingStatHandler()

    private val ID                  = "ralec001"
    private val START_DATE          = "2025-04-01"
    private val END_DATE            = "2025-04-02"
    private val EMPTY_END_DATE      = ""
    private val CURRENT_END_DATE    = "2025-04-03"
    private val STAT_LIST           = mutableListOf(BattingStat.HIT)

    private val FIRST_GID       = "GID1"
    private val SECOND_GID      = "GID2"
    private val TEMP            = 69
    private val WIND_SPEED      = 420
    private val WIND_DIRECTION  = WindDirection.FROM_RIGHT_FIELD
    private val CONDITION       = Condition.OVERCAST
    private val PRECIPITATION   = Precipitation.DRIZZLE
    private val TIME_OF_DAY     = TimeOfDay.NIGHT
    private val HOME_TEAM       = Team.SEA
    private val AWAY_TEAM       = Team.SDN

    private val STAT_NAME           = BattingStat.HIT
    private val FIRST_HIT_NUM       = 2.0
    private val SECOND_HIT_NUM      = 2.0

    private val FIRST_GAME                                  = Game(FIRST_GID, TEMP, WIND_SPEED, WIND_DIRECTION, CONDITION, PRECIPITATION, TIME_OF_DAY, HOME_TEAM, AWAY_TEAM)
    private val SECOND_GAME                                 = Game(SECOND_GID, TEMP, WIND_SPEED, WIND_DIRECTION, CONDITION, PRECIPITATION, TIME_OF_DAY, HOME_TEAM, AWAY_TEAM)
    private val FIRST_BATTING_GAME_STAT                     = BattingGameStat(STAT_NAME, FIRST_HIT_NUM)
    private val SECOND_BATTING_GAME_STAT                    = BattingGameStat(STAT_NAME, SECOND_HIT_NUM)
    private val FIRST_BATTING_GAME                          = BattingGame(FIRST_GAME, mutableListOf(FIRST_BATTING_GAME_STAT))
    private val SECOND_BATTING_GAME                         = BattingGame(SECOND_GAME, mutableListOf(SECOND_BATTING_GAME_STAT))
    private val HOLISTIC_BATTING_STAT_LIST                  = HolisticBattingStatList(ID, listOf(FIRST_BATTING_GAME))
    private val HOLISTIC_BATTING_STAT_LIST_EMPTY_END_DATE   = HolisticBattingStatList(ID, listOf(FIRST_BATTING_GAME, SECOND_BATTING_GAME))

    @Before
    fun beforeTests()
    {
        mockkObject(PlayerBattingSql)

        every { PlayerBattingSql.getBattingStatsAggregate(ID, START_DATE, END_DATE          , STAT_LIST) } returns HOLISTIC_BATTING_STAT_LIST
        every { PlayerBattingSql.getBattingStatsAggregate(ID, START_DATE, CURRENT_END_DATE  , STAT_LIST) } returns HOLISTIC_BATTING_STAT_LIST_EMPTY_END_DATE

        mockkObject(DateHelper)

        every { DateHelper.getCurrentDate() } returns CURRENT_END_DATE
    }

    @After
    fun afterTest()
    {
        unmockkAll()
    }

    @Test
    fun testGetStats_whenFinalDateIsNotEmpty_returnsValidStats()
    {
        val stats = this.helper.getStats(ID, START_DATE, END_DATE, STAT_LIST)

        assertEquals(HOLISTIC_BATTING_STAT_LIST, stats)
    }

    @Test
    fun testGetStats_whenFinalDateIsEmpty_returnsValidStats()
    {
        val stats = this.helper.getStats(ID, START_DATE, EMPTY_END_DATE, STAT_LIST)

        assertEquals(HOLISTIC_BATTING_STAT_LIST_EMPTY_END_DATE, stats)
    }
}