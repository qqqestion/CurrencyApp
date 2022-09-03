package ru.tashkent.currencyapp.data.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CurrencyInfoRemote(
    val code: String,
    val decimal_digits: Int,
    val name: String,
    val name_plural: String,
    val rounding: Int,
    val symbol: String,
    val symbol_native: String
)