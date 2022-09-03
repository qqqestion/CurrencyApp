package ru.tashkent.currencyapp.data.remote

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApi {
    @GET("latest")
    fun getLatest(
        @Query("base_currency") base: String,
        @Query("currencies") symbols: String
    ): Call<RemoteLatestResponse>

    @GET("currencies")
    fun getSymbols(): Call<RemoteCurrenciesResponse>
}