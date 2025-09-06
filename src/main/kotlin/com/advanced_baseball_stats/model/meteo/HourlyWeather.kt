package com.advanced_baseball_stats.model.meteo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//TODO: Read date from json as DATE instead of STRING
@Serializable
class HourlyWeather(@SerialName("time")
                    val time: List<String>,
                    @SerialName("temperature_2m")
                    val temperature: List<Double>,
                    @SerialName("wind_speed_10m")
                    val windSpeed: List<Double>) {
}