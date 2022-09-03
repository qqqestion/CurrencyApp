package ru.tashkent.currencyapp.data.remote

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Meta(
    val last_updated_at: String
)