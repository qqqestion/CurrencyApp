package ru.tashkent.currencyapp.data.sharedprefs

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ru.tashkent.currencyapp.data.SortOption.ALPHABETICAL_ASCENDING
import javax.inject.Inject

class DataStoreManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    suspend fun <T> write(preference: DataStorePreference<T>, value: T) {
        dataStore.edit { preferences ->
            preferences[preference.key] = value
        }
    }

    suspend fun <T> read(preference: DataStorePreference<T>) =
        dataStore.data.first()[preference.key] ?: preference.defaultValue

    fun <T> observe(preference: DataStorePreference<T>) = dataStore.data.map { preferences ->
        preferences[preference.key] ?: preference.defaultValue
    }
}

sealed class DataStorePreference<T>(
    val key: Preferences.Key<T>,
    val defaultValue: T
) {
    object CurrencyBase : DataStorePreference<String>(stringPreferencesKey("currency_base"), "RUB")
    object LastCurrenciesUpdate : DataStorePreference<Long>(longPreferencesKey("last_currencies_update"), 0L)
    object LastRequestedBase : DataStorePreference<String>(stringPreferencesKey("last_requested_base"), "RUB")
    object LastFavouriteUpdate : DataStorePreference<Long>(longPreferencesKey("last_favourite_update"), 0L)
    object LastCurrencyNamesUpdate : DataStorePreference<Long>(longPreferencesKey("last_currency_names_update"), 0L)
    object SortOption : DataStorePreference<String>(stringPreferencesKey("sort_option"), ALPHABETICAL_ASCENDING.name)
}