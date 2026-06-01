package com.corsolp.data.remote.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherRemoteModel(
    @Json(name = "current_weather")
    val currentWeather: CurrentWeather
)

@JsonClass(generateAdapter = true)
data class CurrentWeather(
    @Json(name = "temperature")
    val temperature: Double,

    @Json(name = "weathercode")
    val weathercode: Int
)