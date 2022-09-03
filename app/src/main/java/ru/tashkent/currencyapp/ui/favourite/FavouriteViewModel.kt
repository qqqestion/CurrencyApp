package ru.tashkent.currencyapp.ui.favourite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.tashkent.currencyapp.data.repository.CurrencyRepository
import ru.tashkent.currencyapp.mapper.CurrencyRateMapper
import ru.tashkent.currencyapp.ui.common.CurrencyRateUi
import ru.tashkent.currencyapp.ui.common.ErrorManager
import ru.tashkent.currencyapp.ui.common.ResourceError
import ru.tashkent.currencyapp.ui.common.collectStatus
import ru.tashkent.currencyapp.usecase.UpdateFavouriteCurrencies
import javax.inject.Inject

@HiltViewModel
class FavouriteViewModel @Inject constructor(
    private val currencyRepository: CurrencyRepository,
    private val updateFavouriteCurrencies: UpdateFavouriteCurrencies,
    private val errorManager: ErrorManager,
    currencyRateMapper: CurrencyRateMapper
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val state = combine(
        currencyRepository.observeFavouriteCurrencies(),
        _isLoading,
        errorManager.errors
    ) { currencies, isLoading, error ->
        FavouriteState(
            currencies.map(currencyRateMapper::toUi),
            isLoading = isLoading,
            error = error
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(2000),
        FavouriteState(isLoading = true)
    )

    fun toggleFavourite(currency: CurrencyRateUi) {
        viewModelScope.launch {
            currencyRepository.toggleFavourite(currency.symbol, currency.isFavourite)
        }
    }

    init {
        refresh()
        viewModelScope.launch {
            currencyRepository.observeBaseCurrencyChange().collect { hasChanged ->
                if (hasChanged) {
                    refresh()
                }
            }
        }
    }

    fun refresh() {
        updateFavouriteCurrencies().collectStatus(
            scope = viewModelScope,
            onStarted = { _isLoading.value = true },
            onSuccess = { _isLoading.value = false },
            onError = { error ->
                errorManager.addError(ResourceError(error))
                _isLoading.value = false
            }
        )
    }

    fun dismissError() {
        errorManager.dismissError()
    }
}