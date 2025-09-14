package com.advanced_baseball_stats.handler.batting

import com.advanced_baseball_stats.common.BattingStatCommons.EMPTY_PITCHER_ID
import com.advanced_baseball_stats.common.BattingStatCommons.EMPTY_PITCHER_HANDEDNESS
import com.advanced_baseball_stats.common.BattingStatCommons.END_DATE
import com.advanced_baseball_stats.common.BattingStatCommons.FIRST_GAME
import com.advanced_baseball_stats.common.BattingStatCommons.HIT_STAT
import com.advanced_baseball_stats.common.BattingStatCommons.HOLISTIC_BATTING_STAT_LIST_HIT
import com.advanced_baseball_stats.common.BattingStatCommons.ID
import com.advanced_baseball_stats.common.BattingStatCommons.START_DATE
import com.advanced_baseball_stats.common.BattingStatCommons.STAT_LIST
import com.advanced_baseball_stats.exception.UnknownBattingStatException
import com.advanced_baseball_stats.exception.UnknownPeriodException
import com.advanced_baseball_stats.model.batting.BattingGame
import com.advanced_baseball_stats.model.batting.BattingGameStat
import com.advanced_baseball_stats.model.batting.BattingStat
import com.advanced_baseball_stats.model.batting.HolisticBattingStatList
import com.advanced_baseball_stats.model.common.Period
import com.advanced_baseball_stats.utility.converter.BattingStatConverter
import com.advanced_baseball_stats.utility.converter.PeriodConverter
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlin.test.assertEquals
import org.junit.jupiter.api.assertThrows

import org.junit.After
import org.junit.Before
import org.junit.Test

class BattingStatHandlerTest
{
    private val perGameBattingStatHandler       = mockk<PerGameBattingStatHandler>  ()
    private val aggregateGameBattingStatHandler = mockk<AggregateBattingStatHandler>()
    private val totalBattingStatHandler         = mockk<TotalBattingStatHandler>    ()

    private val handler = BattingStatHandler(perGameBattingStatHandler, aggregateGameBattingStatHandler, totalBattingStatHandler)

    private val INVALID_BATTING_STAT        = "INVALID_BATTING_STAT"
    private val BATTING_STAT_HIT            = "HIT"
    private val BATTING_STAT_RUN            = "RUN"
    private val BATTING_STAT_RBI            = "RBI"
    private val RUN_STAT                    = BattingStat.RUN
    private val RBI_STAT                    = BattingStat.RBI
    private val INVALID_STAT_LIST           = mutableListOf(INVALID_BATTING_STAT)
    private val BATTING_STAT_LIST_HIT       = mutableListOf(BATTING_STAT_HIT)
    private val BATTING_STAT_LIST_RUN       = mutableListOf(BATTING_STAT_RUN)
    private val BATTING_STAT_LIST_RBI       = mutableListOf(BATTING_STAT_RBI)
    private val PER_GAME_PERIOD             = "PER_GAME"
    private val AGGREGATE_PERIOD            = "AGGREGATE"
    private val TOTAL_PERIOD                = "TOTAL"
    private val INVALID_PERIOD              = "INVALID_PERIOD"
    private val CONVERTED_PER_GAME_PERIOD   = Period.PER_GAME
    private val CONVERTED_AGGREGATE_PERIOD  = Period.AGGREGATE
    private val CONVERTED_TOTAL_PERIOD      = Period.TOTAL


    private val STAT_NAME_RUN               = BattingStat.RUN
    private val STAT_LIST_RUN               = mutableListOf(STAT_NAME_RUN)
    private val RUN_NUM                     = 10.0
    private val BATTING_GAME_STAT_RUN       = BattingGameStat(STAT_NAME_RUN, RUN_NUM)
    private val BATTING_GAME_RUN            = BattingGame(FIRST_GAME, mutableListOf(BATTING_GAME_STAT_RUN))

    private val STAT_NAME_RBI               = BattingStat.RBI
    private val STAT_LIST_RBI               = mutableListOf(STAT_NAME_RBI)
    private val RBI_NUM                     = 12.0
    private val BATTING_GAME_STAT_RBI       = BattingGameStat(STAT_NAME_RBI, RBI_NUM)

    private val HOLISTIC_BATTING_STAT_LIST_RUNS = HolisticBattingStatList(ID, listOf(BATTING_GAME_RUN))

    @Before
    fun beforeTests()
    {
        mockkObject(BattingStatConverter)

        every { BattingStatConverter.convertBattingStat(INVALID_BATTING_STAT) } throws UnknownBattingStatException("Unknown batting stat!")
        every { BattingStatConverter.convertBattingStat(BATTING_STAT_HIT    ) } returns HIT_STAT
        every { BattingStatConverter.convertBattingStat(BATTING_STAT_RUN    ) } returns RUN_STAT
        every { BattingStatConverter.convertBattingStat(BATTING_STAT_RBI    ) } returns RBI_STAT

        mockkObject(PeriodConverter)

        every { PeriodConverter.convertPeriod(INVALID_PERIOD    ) } throws UnknownPeriodException("Unknown period!")
        every { PeriodConverter.convertPeriod(PER_GAME_PERIOD   ) } returns CONVERTED_PER_GAME_PERIOD
        every { PeriodConverter.convertPeriod(AGGREGATE_PERIOD  ) } returns CONVERTED_AGGREGATE_PERIOD
        every { PeriodConverter.convertPeriod(TOTAL_PERIOD      ) } returns CONVERTED_TOTAL_PERIOD

        every { perGameBattingStatHandler       .getStats(ID, EMPTY_PITCHER_ID, EMPTY_PITCHER_HANDEDNESS,   START_DATE, END_DATE, STAT_LIST         ) } returns HOLISTIC_BATTING_STAT_LIST_HIT
        every { aggregateGameBattingStatHandler .getStats(ID,                                               START_DATE, END_DATE, STAT_LIST_RUN     ) } returns HOLISTIC_BATTING_STAT_LIST_RUNS
        every { totalBattingStatHandler         .getStats(ID, EMPTY_PITCHER_ID, EMPTY_PITCHER_HANDEDNESS,   START_DATE, END_DATE, STAT_LIST_RBI     ) } returns listOf(BATTING_GAME_STAT_RBI)
    }

    @After
    fun afterTests()
    {
        unmockkObject(BattingStatConverter  )
        unmockkObject(PeriodConverter       )
    }

    @Test
    fun testGetBattingStats_whenBattingStatConverterThrowsUnknownBattingStatException_throwsException()
    {
        assertThrows<UnknownBattingStatException> {
            this.handler.getBattingStats(ID, EMPTY_PITCHER_ID, EMPTY_PITCHER_HANDEDNESS, PER_GAME_PERIOD, START_DATE, END_DATE, INVALID_STAT_LIST)
        }
    }

    @Test
    fun testGetBattingStats_whenBattingStatConverterThrowsUnknownPeriodException_throwsException()
    {
        assertThrows<UnknownPeriodException> {
            this.handler.getBattingStats(ID, EMPTY_PITCHER_ID, EMPTY_PITCHER_HANDEDNESS, INVALID_PERIOD, START_DATE, END_DATE, BATTING_STAT_LIST_HIT)
        }
    }

    @Test
    fun testGetBattingStats_whenPerGamePeriodIsPassed_returnsValidBattingStats()
    {
        val stats = handler.getBattingStats(ID, EMPTY_PITCHER_ID, EMPTY_PITCHER_HANDEDNESS, PER_GAME_PERIOD, START_DATE, END_DATE, BATTING_STAT_LIST_HIT)

        assertEquals(HOLISTIC_BATTING_STAT_LIST_HIT, stats)
    }

    @Test
    fun testGetBattingStats_whenAggregatePeriodIsPassed_returnsValidBattingStats()
    {
        val stats = handler.getBattingStats(ID, EMPTY_PITCHER_ID, EMPTY_PITCHER_HANDEDNESS, AGGREGATE_PERIOD, START_DATE, END_DATE, BATTING_STAT_LIST_RUN)

        assertEquals(HOLISTIC_BATTING_STAT_LIST_RUNS, stats)
    }

    @Test
    fun testGetBattingStats_whenTotalPeriodIsPassed_returnsValidBattingStats()
    {
        val stats = handler.getBattingStats(ID, EMPTY_PITCHER_ID, EMPTY_PITCHER_HANDEDNESS, TOTAL_PERIOD, START_DATE, END_DATE, BATTING_STAT_LIST_RBI)

        assertEquals(listOf(BATTING_GAME_STAT_RBI), stats)
    }
}