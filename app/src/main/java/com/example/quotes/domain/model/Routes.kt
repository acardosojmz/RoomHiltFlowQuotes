package com.example.quotes.domain.model

sealed class Routes(val route: String) {
    data object List : Routes("listQuotes")
    data object Add : Routes("addQuote")
}