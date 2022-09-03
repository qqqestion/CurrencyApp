package ru.tashkent.currencyapp.ui.common

data class CurrencyRateUi(
    val symbol: String,
    val rate: String,
    val name: String,
    val isFavourite: Boolean
)
