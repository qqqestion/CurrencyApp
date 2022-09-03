package ru.tashkent.currencyapp.data

data class CurrencyRate(
    val symbol: String,
    val rate: Double,
    val name: String,
    val isFavourite: Boolean
)
