package com.advanced_baseball_stats.handler.batting

import com.advanced_baseball_stats.common.BattingStatCommons.CURRENT_END_DATE
import com.advanced_baseball_stats.common.BattingStatCommons.EMPTY_PITCHER_ID
import com.advanced_baseball_stats.common.BattingStatCommons.EMPTY_END_DATE
import com.advanced_baseball_stats.common.BattingStatCommons.EMPTY_PITCHER_HANDEDNESS
import com.advanced_baseball_stats.common.BattingStatCommons.END_DATE
import com.advanced_baseball_stats.common.BattingStatCommons.FIRST_BATTING_GAME_STAT_HIT
import com.advanced_baseball_stats.common.BattingStatCommons.ID
import com.advanced_baseball_stats.common.BattingStatCommons.SECOND_BATTING_GAME_STAT_HIT
import com.advanced_baseball_stats.common.BattingStatCommons.START_DATE
import com.advanced_baseball_stats.common.BattingStatCommons.STAT_LIST
import com.advanced_baseball_stats.repository.PlaysSql
import com.advanced_baseball_stats.utility.converter.date.DateHelper
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlin.test.assertEquals

import org.junit.After
import org.junit.Before
import org.junit.Test

class TotalBattingStatHandlerTest
{
    private val handler = TotalBattingStatHandler()

    @Before
    fun beforeTest()
    {
        mockkObject(PlaysSql)

        every { PlaysSql.getBattingStatsTotalByPitcher(ID, EMPTY_PITCHER_ID, EMPTY_PITCHER_HANDEDNESS, START_DATE, END_DATE,            STAT_LIST) } returns listOf(FIRST_BATTING_GAME_STAT_HIT)
        every { PlaysSql.getBattingStatsTotalByPitcher(ID, EMPTY_PITCHER_ID, EMPTY_PITCHER_HANDEDNESS, START_DATE, CURRENT_END_DATE,    STAT_LIST) } returns listOf(FIRST_BATTING_GAME_STAT_HIT, SECOND_BATTING_GAME_STAT_HIT)

        mockkObject(DateHelper)

        every { DateHelper.getCurrentDate() } returns CURRENT_END_DATE
    }

    @After
    fun afterTest()
    {
        unmockkAll()
    }

    @Test
    fun testGetStats_whenEndDateIsNotEmpty_returnsValidStats()
    {
        val stats = this.handler.getStats(ID, EMPTY_PITCHER_ID, EMPTY_PITCHER_HANDEDNESS, START_DATE, END_DATE, STAT_LIST)

        assertEquals(listOf(FIRST_BATTING_GAME_STAT_HIT), stats)
    }

    @Test
    fun testGetStats_whenEndDateIsEmpty_returnsValidStats()
    {
        val stats = this.handler.getStats(ID, EMPTY_PITCHER_ID, EMPTY_PITCHER_HANDEDNESS, START_DATE, EMPTY_END_DATE, STAT_LIST)

        assertEquals(listOf(FIRST_BATTING_GAME_STAT_HIT, SECOND_BATTING_GAME_STAT_HIT), stats)
    }
}