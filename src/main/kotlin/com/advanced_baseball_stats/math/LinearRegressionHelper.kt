package com.advanced_baseball_stats.math

import com.advanced_baseball_stats.model.math.LinearRegression

import kotlin.math.pow
import kotlin.math.sqrt

class LinearRegressionHelper
{
    fun getLinearRegression(x: List<Double>, y: List<Double>) : LinearRegression?
    {
        if (x.size != y.size)
        {
            return null
        }

        val normalizedStats = this.getNormalizedValues(x, y)

        val normalizedX = normalizedStats.first
        val normalizedY = normalizedStats.second


        val xAvg = normalizedX.average()
        val yAvg = normalizedY.average()

        val sVals = this.getS(normalizedX, normalizedY, xAvg, yAvg)

        val sXy = sVals.first
        val sXx = sVals.second

        val slope       = sXy / sXx
        val intercept   = yAvg - slope * xAvg

        val sortedX = normalizedX.sortedBy { it }

        val points: MutableList<Double> = mutableListOf()

        for (xVal in sortedX)
        {
            val cur = xVal * slope + intercept

            points.add(cur)
        }

        val rValue          = this.getRValue        (normalizedX, normalizedY, xAvg, yAvg)
        val standardError   = this.getStandardError (normalizedY, points)

        return LinearRegression(slope, intercept, rValue, standardError, points, normalizedX, normalizedY)
    }

    private fun getNormalizedValues(x: List<Double>, y: List<Double>) : Pair<List<Double>, List<Double>>
    {
        val xCount  : MutableMap<Double, Double> = mutableMapOf()
        val xSum    : MutableMap<Double, Double> = mutableMapOf()

        for (i in x.indices)
        {
            val curX = x[i]
            val curY = y[i]

            if (xCount.containsKey(curX))
            {
                val curCount = xCount[curX]
                xCount.replace(curX, curCount!! + 1.0)
            }
            else
            {
                xCount[curX] = 1.0
            }

            if (xSum.containsKey(curX))
            {
                val curSum = xSum[curX]
                xSum.replace(curX, curSum!! + curY)
            }
            else
            {
                xSum[curX] = curY
            }
        }

        val normalizedX : MutableList<Double> = mutableListOf()
        val normalizedY : MutableList<Double> = mutableListOf()

        for (xVal in xCount.keys)
        {
            val curSum      = xSum  [xVal]
            val curCount    = xCount[xVal]

            if (curSum != null && curCount != null)
            {
                val curAverage = curSum / curCount

                normalizedX.add(xVal)
                normalizedY.add(curAverage)
            }
        }

        return Pair(normalizedX, normalizedY)
    }

    private fun getS(x: List<Double>, y: List<Double>, xAvg: Double, yAvg: Double): Pair<Double, Double>
    {
        var sXy = 0.0
        var sXx = 0.0

        for (i in x.indices)
        {
            val curX = x[i]
            val curY = y[i]

            val diffX = curX - xAvg
            val diffY = curY - yAvg

            sXy += diffX * diffY
            sXx += diffX.pow(2)
        }

        return Pair(sXy, sXx)
    }

    private fun getRValue(x: List<Double>, y: List<Double>, xAvg: Double, yAvg: Double) : Double
    {
        var diffSum          = 0.0
        var squaredDiffSumX  = 0.0
        var squaredDiffSumY  = 0.0

        for (i in x.indices)
        {
            val curX = x[i];
            val curY = y[i];

            val diffX = curX - xAvg
            val diffY = curY - yAvg

            diffSum += diffX * diffY

            squaredDiffSumX += diffX.pow(2)
            squaredDiffSumY += diffY.pow(2)
        }

        val totalSquaredDiffSum = sqrt(squaredDiffSumX * squaredDiffSumY)

        return diffSum / totalSquaredDiffSum
    }

    private fun getStandardError(y: List<Double>, estimatedY: List<Double>) : Double
    {
        var squaredDiff = 0.0

        for (i in y.indices)
        {
            val curY            = y         [i]
            val curEstimatedY   = estimatedY[i]

            squaredDiff += (curEstimatedY - curY).pow(2)
        }

        val standardError = sqrt(squaredDiff / ( y.size - 2.0 ))

        return standardError
    }
}