package com.advanced_baseball_stats.handler.batting

import com.advanced_baseball_stats.common.BattingStatCommons.AWAY_TEAM
import com.advanced_baseball_stats.common.BattingStatCommons.CONDITION
import com.advanced_baseball_stats.common.BattingStatCommons.CURRENT_END_DATE
import com.advanced_baseball_stats.common.BattingStatCommons.EMPTY_END_DATE
import com.advanced_baseball_stats.common.BattingStatCommons.EMPTY_PITCHER_HANDEDNESS
import com.advanced_baseball_stats.common.BattingStatCommons.EMPTY_PITCHER_ID
import com.advanced_baseball_stats.common.BattingStatCommons.END_DATE
import com.advanced_baseball_stats.common.BattingStatCommons.FIRST_BATTING_GAME_STAT_HIT
import com.advanced_baseball_stats.common.BattingStatCommons.HOLISTIC_BATTING_STAT_LIST_HIT
import com.advanced_baseball_stats.common.BattingStatCommons.HOLISTIC_BATTING_STAT_LIST_HIT_EMPTY_END_DATE
import com.advanced_baseball_stats.common.BattingStatCommons.HOME_TEAM
import com.advanced_baseball_stats.common.BattingStatCommons.ID
import com.advanced_baseball_stats.common.BattingStatCommons.PRECIPITATION
import com.advanced_baseball_stats.common.BattingStatCommons.SECOND_BATTING_GAME_STAT_HIT
import com.advanced_baseball_stats.common.BattingStatCommons.START_DATE
import com.advanced_baseball_stats.common.BattingStatCommons.STAT_LIST
import com.advanced_baseball_stats.common.BattingStatCommons.TEMP
import com.advanced_baseball_stats.common.BattingStatCommons.TIME_OF_DAY
import com.advanced_baseball_stats.common.BattingStatCommons.WIND_DIRECTION
import com.advanced_baseball_stats.common.BattingStatCommons.WIND_SPEED
import com.advanced_baseball_stats.model.batting.BattingGame
import com.advanced_baseball_stats.model.batting.HolisticBattingStatList
import com.advanced_baseball_stats.model.game.*
import com.advanced_baseball_stats.repository.PlayerBattingSql
import com.advanced_baseball_stats.repository.PlaysSql
import com.advanced_baseball_stats.utility.converter.date.DateHelper
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll

import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class PerGameBattingStatHandlerTest
{
    private val handler = PerGameBattingStatHandler()

    private val PITCHER_ID                  = "valdf001"
    private val PITCHER_HANDEDNESS          = "R"

    private val THIRD_GID   = "GID3"
    private val FOURTH_GID  = "GID4"

    private val THIRD_GAME                                              = Game(THIRD_GID, TEMP, WIND_SPEED, WIND_DIRECTION, CONDITION, PRECIPITATION, TIME_OF_DAY, HOME_TEAM, AWAY_TEAM)
    private val FOURTH_GAME                                             = Game(FOURTH_GID, TEMP, WIND_SPEED, WIND_DIRECTION, CONDITION, PRECIPITATION, TIME_OF_DAY, HOME_TEAM, AWAY_TEAM)
    private val THIRD_BATTING_GAME                                      = BattingGame(THIRD_GAME, mutableListOf(FIRST_BATTING_GAME_STAT_HIT))
    private val FOURTH_BATTING_GAME                                     = BattingGame(FOURTH_GAME, mutableListOf(SECOND_BATTING_GAME_STAT_HIT))
    private val HOLISTIC_BATTING_STAT_LIST_FROM_PLAYS                   = HolisticBattingStatList(ID, listOf(THIRD_BATTING_GAME))
    private val HOLISTIC_BATTING_STAT_LIST_FROM_PLAYS_EMPTY_END_DATE    = HolisticBattingStatList(ID, listOf(THIRD_BATTING_GAME, FOURTH_BATTING_GAME))

    @Before
    fun beforeTests()
    {
        mockkObject(PlayerBattingSql)

        every { PlayerBattingSql.getBattingStatsPerGame(ID, START_DATE, END_DATE            , STAT_LIST) } returns HOLISTIC_BATTING_STAT_LIST_HIT
        every { PlayerBattingSql.getBattingStatsPerGame(ID, START_DATE, CURRENT_END_DATE    , STAT_LIST) } returns HOLISTIC_BATTING_STAT_LIST_HIT_EMPTY_END_DATE

        mockkObject(PlaysSql)

        every { PlaysSql.getBattingStatsPerGameByPitcher(ID, PITCHER_ID, EMPTY_PITCHER_HANDEDNESS, START_DATE, END_DATE           , STAT_LIST) } returns HOLISTIC_BATTING_STAT_LIST_FROM_PLAYS
        every { PlaysSql.getBattingStatsPerGameByPitcher(ID, PITCHER_ID, EMPTY_PITCHER_HANDEDNESS, START_DATE, CURRENT_END_DATE   , STAT_LIST) } returns HOLISTIC_BATTING_STAT_LIST_FROM_PLAYS_EMPTY_END_DATE

        every { PlaysSql.getBattingStatsPerGameByPitcher(ID, EMPTY_PITCHER_ID, PITCHER_HANDEDNESS, START_DATE, END_DATE           , STAT_LIST) } returns HOLISTIC_BATTING_STAT_LIST_FROM_PLAYS
        every { PlaysSql.getBattingStatsPerGameByPitcher(ID, EMPTY_PITCHER_ID, PITCHER_HANDEDNESS, START_DATE, CURRENT_END_DATE   , STAT_LIST) } returns HOLISTIC_BATTING_STAT_LIST_FROM_PLAYS_EMPTY_END_DATE

        every { PlaysSql.getBattingStatsPerGameByPitcher(ID, PITCHER_ID, PITCHER_HANDEDNESS, START_DATE, END_DATE           , STAT_LIST) } returns HOLISTIC_BATTING_STAT_LIST_FROM_PLAYS
        every { PlaysSql.getBattingStatsPerGameByPitcher(ID, PITCHER_ID, PITCHER_HANDEDNESS, START_DATE, CURRENT_END_DATE   , STAT_LIST) } returns HOLISTIC_BATTING_STAT_LIST_FROM_PLAYS_EMPTY_END_DATE

        mockkObject(DateHelper)

        every { DateHelper.getCurrentDate() } returns CURRENT_END_DATE
    }

    @Test
    fun testGetStats_whenEndDateIsNotEmptyAndPitcherStatsAreEmpty_returnsValidStatsFromPlayerBattingSql()
    {
        val stats = this.handler.getStats(ID, EMPTY_PITCHER_ID, EMPTY_PITCHER_HANDEDNESS, START_DATE, END_DATE, STAT_LIST)

        assertEquals(HOLISTIC_BATTING_STAT_LIST_HIT, stats)
    }

    @Test
    fun testGetStats_whenEndDateIsEmptyAndPitcherStatsAreEmpty_returnsValidStatsFromPlayerBattingSql()
    {
        val stats = this.handler.getStats(ID, EMPTY_PITCHER_ID, EMPTY_PITCHER_HANDEDNESS, START_DATE, EMPTY_END_DATE, STAT_LIST)

        assertEquals(HOLISTIC_BATTING_STAT_LIST_HIT_EMPTY_END_DATE, stats)
    }

    @Test
    fun testGetStats_whenEndDateIsNotEmptyAndPitcherIdIsNotEmpty_returnsValidStatsFromPlaysSql()
    {
        val stats = this.handler.getStats(ID, PITCHER_ID, EMPTY_PITCHER_HANDEDNESS, START_DATE, END_DATE, STAT_LIST)

        assertEquals(HOLISTIC_BATTING_STAT_LIST_FROM_PLAYS, stats)
    }

    @Test
    fun testGetStats_whenEndDateIsEmptyAndPitcherIdIsNotEmpty_returnsValidStatsFromPlaysSql()
    {
        val stats = this.handler.getStats(ID, PITCHER_ID, EMPTY_PITCHER_HANDEDNESS, START_DATE, EMPTY_END_DATE, STAT_LIST)

        assertEquals(HOLISTIC_BATTING_STAT_LIST_FROM_PLAYS_EMPTY_END_DATE, stats)
    }

    @Test
    fun testGetStats_whenEndDateIsNotEmptyAndPitcherHandednessIsNotEmpty_returnsValidStatsFromPlaysSql()
    {
        val stats = this.handler.getStats(ID, EMPTY_PITCHER_ID, PITCHER_HANDEDNESS, START_DATE, END_DATE, STAT_LIST)

        assertEquals(HOLISTIC_BATTING_STAT_LIST_FROM_PLAYS, stats)
    }

    @Test
    fun testGetStats_whenEndDateIsEmptyAndPitcherHandednessIsNotEmpty_returnsValidStatsFromPlaysSql()
    {
        val stats = this.handler.getStats(ID, EMPTY_PITCHER_ID, PITCHER_HANDEDNESS, START_DATE, EMPTY_END_DATE, STAT_LIST)

        assertEquals(HOLISTIC_BATTING_STAT_LIST_FROM_PLAYS_EMPTY_END_DATE, stats)
    }

    @Test
    fun testGetStats_whenEndDateIsNotEmptyAndPitcherIdIsNotEmptyAndPitcherHandednessIsNotEmpty_returnsValidStatsFromPlaysSql()
    {
        val stats = this.handler.getStats(ID, PITCHER_ID, PITCHER_HANDEDNESS, START_DATE, END_DATE, STAT_LIST)

        assertEquals(HOLISTIC_BATTING_STAT_LIST_FROM_PLAYS, stats)
    }

    @Test
    fun testGetStats_whenEndDateIsEmptyAndPitcherIdIsNotEmptyAndPitcherHandednessIsNotEmpty_returnsValidStatsFromPlaysSql()
    {
        val stats = this.handler.getStats(ID, PITCHER_ID, PITCHER_HANDEDNESS, START_DATE, EMPTY_END_DATE, STAT_LIST)

        assertEquals(HOLISTIC_BATTING_STAT_LIST_FROM_PLAYS_EMPTY_END_DATE, stats)
    }

    @After
    fun afterTest()
    {
        unmockkAll()
    }
}