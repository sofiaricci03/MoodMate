package com.corsolp.domain.repository

import com.corsolp.domain.models.Weather

interface WeatherRepository {
    suspend fun getCurrentWeather(lat: Double, lon: Double): Weather?
}