package com.corsolp.data.repository

import com.corsolp.data.remote.api.WeatherApi
import com.corsolp.domain.models.Weather
import com.corsolp.domain.repository.WeatherRepository

class WeatherRepositoryImpl(
    private val weatherApi: WeatherApi
) : WeatherRepository {

    override suspend fun getCurrentWeather(lat: Double, lon: Double): Weather? {
        return try {
            val response = weatherApi.getCurrentWeather(lat, lon)

            Weather(
                temperature = response.currentWeather.temperature,
                weatherCode = response.currentWeather.weatherCode
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
