package com.advanced_baseball_stats.data.source

import com.advanced_baseball_stats.model.meteo.MeteoWeather
import io.ktor.client.call.*
import io.ktor.client.request.*

class OpenMeteoWeatherSource
{
    val host = "https://archive-api.open-meteo.com/v1/archive"

    suspend fun getWeatherOnDate(date: String)
    {
        val client      = CioHttpClient.getClient()
        val response    = client.get(host)
        {
            url {
                    parameters.append("latitude"        , "47.5915"                         )
                    parameters.append("longitude"       , "122.3326"                        )
                    parameters.append("start_date"      , date                                    )
                    parameters.append("end_date"        , date                                    )
                    parameters.append("hourly"          , "temperature_2m,wind_speed_10m"   )
                    parameters.append("temperature_unit", "fahrenheit"                      )
                    parameters.append("wind_speed_unit" , "mph"                             )
            }
        }

        if (response.status.value in 200..299)
        {
            val meteoWeather: MeteoWeather = response.body()

            println(meteoWeather.toPrintable())
        }

        client.close()
    }
}