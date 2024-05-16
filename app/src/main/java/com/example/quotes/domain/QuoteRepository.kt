package com.example.quotes.domain

import com.example.quotes.domain.model.QuoteModel
import com.example.quotes.domain.model.QuoteState
import kotlinx.coroutines.flow.Flow

interface QuoteRepository {
    suspend fun getQuotes(): Flow<QuoteState>
    suspend fun getQuoteRandom(): Flow<QuoteState>
    suspend fun getQuote(quoteId:Int): Flow<QuoteState>
    suspend fun editQuote(quoteModel: QuoteModel)
    suspend fun addQuote(quoteModel: QuoteModel)
}