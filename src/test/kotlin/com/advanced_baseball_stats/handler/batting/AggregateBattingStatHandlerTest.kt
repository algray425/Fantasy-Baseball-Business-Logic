package com.advanced_baseball_stats.handler.batting

import com.advanced_baseball_stats.common.BattingStatCommons.CURRENT_END_DATE
import com.advanced_baseball_stats.common.BattingStatCommons.EMPTY_END_DATE
import com.advanced_baseball_stats.common.BattingStatCommons.END_DATE
import com.advanced_baseball_stats.common.BattingStatCommons.HOLISTIC_BATTING_STAT_LIST_HIT
import com.advanced_baseball_stats.common.BattingStatCommons.HOLISTIC_BATTING_STAT_LIST_HIT_EMPTY_END_DATE
import com.advanced_baseball_stats.common.BattingStatCommons.ID
import com.advanced_baseball_stats.common.BattingStatCommons.START_DATE
import com.advanced_baseball_stats.common.BattingStatCommons.STAT_LIST
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
    private val handler = AggregateBattingStatHandler()

    @Before
    fun beforeTests()
    {
        mockkObject(PlayerBattingSql)

        every { PlayerBattingSql.getBattingStatsAggregate(ID, START_DATE, END_DATE          , STAT_LIST) } returns HOLISTIC_BATTING_STAT_LIST_HIT
        every { PlayerBattingSql.getBattingStatsAggregate(ID, START_DATE, CURRENT_END_DATE  , STAT_LIST) } returns HOLISTIC_BATTING_STAT_LIST_HIT_EMPTY_END_DATE

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
        val stats = this.handler.getStats(ID, START_DATE, END_DATE, STAT_LIST)

        assertEquals(HOLISTIC_BATTING_STAT_LIST_HIT, stats)
    }

    @Test
    fun testGetStats_whenEndDateIsEmpty_returnsValidStats()
    {
        val stats = this.handler.getStats(ID, START_DATE, EMPTY_END_DATE, STAT_LIST)

        assertEquals(HOLISTIC_BATTING_STAT_LIST_HIT_EMPTY_END_DATE, stats)
    }
}