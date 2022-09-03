package ru.tashkent.currencyapp.data.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RemoteLatestResponse(
    val data: Map<String, CurrencyRateRemote>,
    val meta: Meta
)