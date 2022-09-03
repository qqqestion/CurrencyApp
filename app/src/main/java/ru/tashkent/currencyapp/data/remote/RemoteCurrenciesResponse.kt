package ru.tashkent.currencyapp.data.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RemoteCurrenciesResponse(
    val data: Map<String, CurrencyInfoRemote?>
)