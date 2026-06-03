package com.corsolp.data.remote.api

import com.corsolp.data.remote.models.QuoteRemoteModel
import retrofit2.http.GET

interface QuoteApi {
    @GET("api/random")
    suspend fun getRandomQuote(): List<QuoteRemoteModel>
}