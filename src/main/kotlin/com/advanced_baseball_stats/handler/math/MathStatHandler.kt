package com.advanced_baseball_stats.handler.math

import com.advanced_baseball_stats.handler.batting.PerGameBattingStatHandler
import com.advanced_baseball_stats.math.LinearRegressionHelper
import com.advanced_baseball_stats.model.batting.BattingGame
import com.advanced_baseball_stats.model.batting.BattingStat
import com.advanced_baseball_stats.model.batting.HolisticBattingStatistic
import com.advanced_baseball_stats.model.math.LinearRegression
import com.advanced_baseball_stats.model.math.MeanValues
import com.advanced_baseball_stats.utility.converter.BattingStatConverter

object MathStatHandler
{
    private val linearRegressionHelper      : LinearRegressionHelper    = LinearRegressionHelper    ()
    private val perGameBattingStatHandler   : PerGameBattingStatHandler = PerGameBattingStatHandler ()

    private const val tempIndicator         : String = "TEMP"
    private const val windSpeedIndicator    : String = "WIND_SPEED"
    private const val windDirectionIndicator: String = "WIND_DIRECTION"

    private val gameStatIndicators: Set<String> = setOf(tempIndicator, windSpeedIndicator, windDirectionIndicator)

    fun getLinearRegression(id: String, xStat: String, yStat: String, startDate: String): LinearRegression
    {
        val yBattingStat = BattingStatConverter.convertBattingStat(yStat.uppercase())

        if (isGameSpecificStat(xStat))
        {
            val yStats = perGameBattingStatHandler.getStats(id, "", "", startDate, "", listOf(yBattingStat))

            val x: MutableList<Double> = mutableListOf()
            val y: MutableList<Double> = mutableListOf()

            for (game in yStats.games) {
                val holisticYStat = game.stats.find { stat -> stat.statName == yBattingStat }

                val curX = getCurXValue(xStat, game)
                val curY = holisticYStat?.num

                if (curY != null)
                {
                    x.add(curX)
                    y.add(curY)
                }
            }

            return linearRegressionHelper.getLinearRegression(x, y) ?: LinearRegression(0.0, 0.0, 0.0, 0.0, mutableListOf(), mutableListOf(), mutableListOf())
        }
        else
        {
            val xBattingStat = BattingStatConverter.convertBattingStat(xStat.uppercase())

            val stats = perGameBattingStatHandler.getStats(id, "", "", startDate, "", listOf(xBattingStat, yBattingStat))

            val x: MutableList<Double> = mutableListOf()
            val y: MutableList<Double> = mutableListOf()

            for (game in stats.games)
            {
                val holisticXStat = game.stats.find { stat -> stat.statName == xBattingStat }
                val holisticYStat = game.stats.find { stat -> stat.statName == yBattingStat }

                val curX = holisticXStat?.num
                val curY = holisticYStat?.num

                if (curX != null && curY != null)
                {
                    x.add(curX)
                    y.add(curY)
                }
            }

            return linearRegressionHelper.getLinearRegression(x, y) ?: LinearRegression(0.0, 0.0, 0.0, 0.0, mutableListOf(), mutableListOf(), mutableListOf())
        }
    }

    fun getMeanValues(id: String, xStat: String, yStat: String, startDate: String) : MeanValues
    {
        val yBattingStat = BattingStatConverter.convertBattingStat(yStat.uppercase())

        val stats = perGameBattingStatHandler.getStats(id, "", "", startDate, "", listOf(yBattingStat))

        val valueCounts : MutableMap<String, Double> = mutableMapOf()
        val valueSums   : MutableMap<String, Double> = mutableMapOf()

        for (game in stats.games)
        {
            val holisticYStat = game.stats.find { stat -> stat.statName == yBattingStat }

            val curX = getCurXValueMean(xStat, game)
            val curY = holisticYStat?.num

            if (valueCounts.containsKey(curX))
            {
                val curCount = valueCounts[curX]
                valueCounts.replace(curX, curCount!! + 1.0)
            }
            else
            {
                valueCounts[curX] = 1.0
            }

            if (valueSums.containsKey(curX))
            {
                val curSum = valueSums[curX]
                valueSums.replace(curX, curSum!! + curY!!)
            }
            else
            {
                valueSums[curX] = curY!!
            }
        }

        val meanValues : MutableMap<String, Double> = mutableMapOf()

        for (xVal in valueCounts.keys)
        {
            val curCount    = valueCounts   [xVal]
            val curSum      = valueSums     [xVal]

            if (curSum != null && curCount != null)
            {
                meanValues[xVal] = curSum / curCount
            }
        }

        val meanCalcs = MeanValues(xStat, yStat, meanValues)

        return meanCalcs
    }

    private fun isGameSpecificStat(stat: String) : Boolean
    {
        return gameStatIndicators.contains(stat)
    }

    private fun getCurXValue(xStat: String, battingGame: BattingGame) : Double
    {
        return when (xStat) {
            "TEMP"          -> battingGame.game.temp       .toDouble()
            "WIND_SPEED"    -> battingGame.game.windSpeed  .toDouble()
            else            -> throw Exception("xStat is not allowed for linear regression!")
        }
    }

    private fun getCurXValueMean(xStat: String, battingGame: BattingGame) : String
    {
        return when (xStat)
        {
            "WIND_DIRECTION"    -> battingGame.game.windDirection.toString()
            "PRECIPITATION"     -> battingGame.game.precipitation.toString()
            "CONDITION"         -> battingGame.game.condition.toString()
            "TIME_OF_DAY"       -> battingGame.game.timeOfDay.toString()
            //"HOME_VS_AWAY"      -> getHomeOrAway(holisticStat)
            else                -> throw Exception("xStat is not allowed for mean value calculation!")
        }
    }

//    private fun getHomeOrAway(holisticStat: HolisticBattingStatistic) : String
//    {
//        return if (holisticStat.playerTeam.equals(holisticStat.game.homeTeam)) "Home" else "Away"
//    }
}