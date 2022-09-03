package ru.tashkent.currencyapp.data.repository

import android.util.Log
import arrow.core.Either
import arrow.core.left
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import retrofit2.Call
import ru.tashkent.currencyapp.data.CurrencyError
import ru.tashkent.currencyapp.data.CurrencyName
import ru.tashkent.currencyapp.data.SortOption
import ru.tashkent.currencyapp.data.db.CurrencyDao
import ru.tashkent.currencyapp.data.db.UpdateIsFavouriteEntity
import ru.tashkent.currencyapp.data.db.UpdateNameEntity
import ru.tashkent.currencyapp.data.db.UpdateRateEntity
import ru.tashkent.currencyapp.data.remote.CurrencyApi
import ru.tashkent.currencyapp.data.remote.CurrencyInfoRemote
import ru.tashkent.currencyapp.data.remote.CurrencyRateRemote
import ru.tashkent.currencyapp.data.sharedprefs.DataStoreManager
import ru.tashkent.currencyapp.data.sharedprefs.DataStorePreference
import ru.tashkent.currencyapp.di.IoDispatcher
import ru.tashkent.currencyapp.mapper.CurrencyRateMapper
import ru.tashkent.currencyapp.ui.common.VoidEither
import ru.tashkent.currencyapp.ui.common.void
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyRepositoryImpl @Inject constructor(
    private val currencyApi: CurrencyApi,
    private val currencyDao: CurrencyDao,
    @IoDispatcher
    private val ioDispatcher: CoroutineDispatcher,
    private val dataStoreManager: DataStoreManager,
    private val currencyRateMapper: CurrencyRateMapper
) : CurrencyRepository {

    override fun observeCurrencies() = dataStoreManager.observe(DataStorePreference.SortOption)
        .map { SortOption.valueOf(it) }
        .flatMapLatest { sortOption ->
            when (sortOption) {
                SortOption.ALPHABETICAL_ASCENDING -> currencyDao.observeAllAbcAsc()
                SortOption.ALPHABETICAL_DESCENDING -> currencyDao.observeAllAbcDesc()
                SortOption.RATE_ASCENDING -> currencyDao.observeAllRateAsc()
                SortOption.RATE_DESCENDING -> currencyDao.observeAllRateDesc()
            }
        }
        .map { currencies -> currencies.map(currencyRateMapper::fromEntity) }
        .flowOn(ioDispatcher)

    override fun observeFavouriteCurrencies() =
        dataStoreManager.observe(DataStorePreference.SortOption)
            .map { SortOption.valueOf(it) }
            .flatMapLatest { sortOption ->
                when (sortOption) {
                    SortOption.ALPHABETICAL_ASCENDING -> currencyDao.observeFavouriteAbcAsc()
                    SortOption.ALPHABETICAL_DESCENDING -> currencyDao.observeFavouriteAbcDesc()
                    SortOption.RATE_ASCENDING -> currencyDao.observeFavouriteRateAsc()
                    SortOption.RATE_DESCENDING -> currencyDao.observeFavouriteRateDesc()
                }
            }
            .map { currencies -> currencies.map(currencyRateMapper::fromEntity) }
            .flowOn(ioDispatcher)

    override fun observeBaseCurrencyChange() = observeBaseCurrency()
        .map {
            it != dataStoreManager.read(DataStorePreference.LastRequestedBase)
        }
        .distinctUntilChanged()
        .flowOn(ioDispatcher)

    override suspend fun updateCurrencies() = withContext(ioDispatcher) {
        val lastUpdate = dataStoreManager.read(DataStorePreference.LastCurrenciesUpdate)
        if (shouldUpdateCurrencies(lastUpdate)) {
            requestLatest("", DataStorePreference.LastCurrenciesUpdate)
        } else {
            Either.void()
        }
    }

    override suspend fun updateFavouriteCurrencies() = withContext(ioDispatcher) {
        val lastUpdate = dataStoreManager.read(DataStorePreference.LastFavouriteUpdate)
        if (shouldUpdateCurrencies(lastUpdate)) {
            val favourites = currencyDao.getAllFavourite()

            if (favourites.isEmpty()) Either.void()
            else {
                val symbols = favourites.joinToString(separator = ",") { it.symbol }
                requestLatest(symbols, DataStorePreference.LastFavouriteUpdate)
            }
        } else {
            Either.void()
        }
    }

    private suspend fun requestLatest(
        symbols: String,
        lastUpdatePreference: DataStorePreference<Long>
    ): VoidEither<CurrencyError> {
        val base = dataStoreManager.read(DataStorePreference.CurrencyBase)
        return handleResponse(
            currencyApi.getLatest(base, symbols)
        ) { response ->
            val wasEmpty = currencyDao.isEmpty()
            updateCurrencyRates(
                response.data.values.toList(), lastUpdatePreference
            )
            dataStoreManager.write(DataStorePreference.LastRequestedBase, base)
            if (wasEmpty) {
                updateCurrencyNames()
            }
            Either.void()
        }
    }

    override suspend fun toggleFavourite(symbol: String, isFavourite: Boolean) {
        currencyDao.update(UpdateIsFavouriteEntity(symbol, !isFavourite))
    }

    override suspend fun setBaseCurrency(symbol: String) {
        withContext(ioDispatcher) {
            dataStoreManager.write(DataStorePreference.CurrencyBase, symbol)
        }
    }

    override fun observeBaseCurrency(): Flow<String> =
        dataStoreManager.observe(DataStorePreference.CurrencyBase)

    override fun observeCurrencyNames(): Flow<List<CurrencyName>> = currencyDao.observeNames()
        .map { currencies ->
            currencies.map { currency ->
                CurrencyName(
                    currency.symbol,
                    currency.name
                )
            }
        }
        .flowOn(ioDispatcher)

    override suspend fun updateCurrencyNames() = withContext(ioDispatcher) {
        if (shouldUpdateCurrencyNames()) {
            handleResponse(currencyApi.getSymbols()) { response ->
                updateCurrencyNames(
                    response.data.values.filterNotNull()
                )
                Either.void()
            }
        } else {
            Either.void()
        }
    }

    override suspend fun setSortOption(sortOption: SortOption) {
        withContext(ioDispatcher) {
            dataStoreManager.write(DataStorePreference.SortOption, sortOption.name)
        }
    }

    private suspend fun shouldUpdateCurrencyNames(): Boolean =
        ONE_DAY + dataStoreManager.read(
            DataStorePreference.LastCurrencyNamesUpdate
        ) < System.currentTimeMillis()

    private suspend fun shouldUpdateCurrencies(lastUpdate: Long): Boolean =
        lastUpdate + TWO_MINUTES < System.currentTimeMillis() || wasBaseCurrencyChanged()

    private suspend fun wasBaseCurrencyChanged() =
        dataStoreManager.read(DataStorePreference.CurrencyBase) != dataStoreManager.read(
            DataStorePreference.LastRequestedBase
        )

    private suspend fun updateCurrencyRates(
        currenciesRemote: List<CurrencyRateRemote>,
        lastUpdatePreference: DataStorePreference<Long>,
    ) {
        currenciesRemote.map {
            UpdateRateEntity(
                it.code,
                1.0 / it.rate,
            )
        }.forEach {
            try {
                currencyDao.insert(it)
            } catch (e: Exception) {
                currencyDao.update(it)
            }
        }
        dataStoreManager.write(lastUpdatePreference, System.currentTimeMillis())
    }

    private suspend fun updateCurrencyNames(currencies: List<CurrencyInfoRemote>) {
        currencyDao.updateNames(
            currencies.map { UpdateNameEntity(it.code, it.name) }
        )
    }

    private suspend fun <T> handleResponse(
        call: Call<T>,
        body: suspend (T) -> VoidEither<CurrencyError>
    ) = try {
        @Suppress("BlockingMethodInNonBlockingContext")
        val response = call.execute()
        when {
            response.isSuccessful -> body(checkNotNull(response.body()))
            response.code() == 401 -> CurrencyError.InvalidApiKey.left()
            else -> CurrencyError.UnknownError.left()
        }
    } catch (e: Throwable) {
        Log.d("!!!", "Error", e)
        when (e) {
            is UnknownHostException, is SocketTimeoutException -> CurrencyError.NoInternet.left()
            else -> CurrencyError.UnknownError.left()
        }
    }

    companion object {
        private const val TWO_MINUTES = 1000 * 60 * 2
        private const val ONE_DAY = 1000 * 60 * 60 * 25
    }
}

