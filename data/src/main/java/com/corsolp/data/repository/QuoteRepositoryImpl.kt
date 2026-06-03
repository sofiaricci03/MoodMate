package com.corsolp.data.repository

import com.corsolp.data.remote.api.RetrofitClient
import com.corsolp.domain.models.Quote
import com.corsolp.domain.repository.QuoteRepository

class QuoteRepositoryImpl : QuoteRepository {
    override suspend fun getRandomQuote(): Quote? {
        return try {
            val response = RetrofitClient.quoteApi.getRandomQuote()
            val firstQuote = response.firstOrNull()

            if (firstQuote != null) {
                Quote(text = firstQuote.quote, author = firstQuote.author)
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}