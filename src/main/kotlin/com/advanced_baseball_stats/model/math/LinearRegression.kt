package com.advanced_baseball_stats.model.math

import kotlinx.serialization.Serializable

@Serializable
class LinearRegression(private val slope: Double, private val intercept: Double, private val rValue: Double, private val standardError: Double, private val points: List<Double>,
    private val xValues: List<Double>, private val yValues: List<Double>)
{
    fun getSlope() : Double
    {
        return this.slope
    }

    fun getIntercept() : Double
    {
        return this.intercept
    }

    fun getRValue() : Double
    {
        return this.rValue
    }

    fun getStandardError() : Double
    {
        return this.standardError
    }

    fun getPoints(): List<Double>
    {
        return this.points
    }
}