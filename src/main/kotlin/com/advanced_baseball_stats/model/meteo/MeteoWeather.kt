package com.advanced_baseball_stats.model.meteo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MeteoWeather(@SerialName("latitude")
                        val latitude: Float,
                        @SerialName("longitude")
                        val longitude: Float,
                        @SerialName("generationtime_ms")
                        val generationtime_ms: Float,
                        @SerialName("utc_offset_seconds")
                        val utc_offset_seconds: Int,
                        @SerialName("timezone")
                        val timezone: String,
                        @SerialName("timezone_abbreviation")
                        val timezone_abbreviation: String,
                        @SerialName("elevation")
                        val elevation: Double,
                        @SerialName("hourly")
                        val hourlyWeather: HourlyWeather
) {

    fun toPrintable(): String
    {
        return "temperature: ${hourlyWeather.temperature}"
    }
}