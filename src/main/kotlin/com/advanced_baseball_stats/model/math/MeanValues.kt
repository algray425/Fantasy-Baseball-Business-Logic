package com.advanced_baseball_stats.model.math

import kotlinx.serialization.Serializable

@Serializable
class MeanValues(private val xStat: String, private val yStat: String, private val values: Map<String, Double>)
{
    fun getXStat(): String
    {
        return this.xStat
    }

    fun getYStat(): String
    {
        return this.yStat
    }

    fun getValues(): Map<String, Double>
    {
        return this.values
    }
}