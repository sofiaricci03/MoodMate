package com.corsolp.data.remote.api

import retrofit2.Retrofit

class RetrofitClient {
    private val base_url = "https://api.open-meteo.com/"

    private val retrofitService = Retrofit
        .Builder()
        .baseUrl(base_url)
        .build()

    val weatherService : WeatherService by lazy {
        retrofitService.create(WeatherService::class.java)
    }
}