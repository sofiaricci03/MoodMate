package com.corsolp.data.remote.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class QuoteRemoteModel(
    @Json(name = "q") val quote: String,
    @Json(name = "a") val author: String
)