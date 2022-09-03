package ru.tashkent.currencyapp.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CurrencyRateRemote(
    val code: String,
    @Json(name = "value") val rate: Double
)