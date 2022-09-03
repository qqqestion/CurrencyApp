package ru.tashkent.currencyapp.data.repository

import kotlinx.coroutines.flow.Flow
import ru.tashkent.currencyapp.data.CurrencyError
import ru.tashkent.currencyapp.data.CurrencyName
import ru.tashkent.currencyapp.data.CurrencyRate
import ru.tashkent.currencyapp.data.SortOption
import ru.tashkent.currencyapp.ui.common.VoidEither

interface CurrencyRepository {
    fun observeCurrencies(): Flow<List<CurrencyRate>>
    fun observeFavouriteCurrencies(): Flow<List<CurrencyRate>>
    fun observeBaseCurrency(): Flow<String>
    fun observeCurrencyNames(): Flow<List<CurrencyName>>
    fun observeBaseCurrencyChange(): Flow<Boolean>
    suspend fun updateCurrencyNames(): VoidEither<CurrencyError>
    suspend fun toggleFavourite(symbol: String, isFavourite: Boolean)
    suspend fun setBaseCurrency(symbol: String)
    suspend fun updateCurrencies(): VoidEither<CurrencyError>
    suspend fun updateFavouriteCurrencies(): VoidEither<CurrencyError>
    suspend fun setSortOption(sortOption: SortOption)
}