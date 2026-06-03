package com.corsolp.domain.repository

import com.corsolp.domain.models.Quote

interface QuoteRepository {
    suspend fun getRandomQuote(): Quote?
}