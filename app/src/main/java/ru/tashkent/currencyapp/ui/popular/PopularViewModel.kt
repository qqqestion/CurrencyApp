package ru.tashkent.currencyapp.ui.popular

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
import ru.tashkent.currencyapp.usecase.UpdateCurrencies
import javax.inject.Inject

@HiltViewModel
class PopularViewModel @Inject constructor(
    private val currencyRepository: CurrencyRepository,
    private val updateCurrencies: UpdateCurrencies,
    private val errorManager: ErrorManager,
    currencyRateMapper: CurrencyRateMapper,
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val state = combine(
        currencyRepository.observeCurrencies(),
        _isLoading,
        errorManager.errors
    ) { currencies, isLoading, error ->
        PopularState(
            currencies.map(currencyRateMapper::toUi),
            isLoading = isLoading,
            error = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(2000),
        initialValue = PopularState(isLoading = true)
    )

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

    fun toggleFavourite(currency: CurrencyRateUi) {
        viewModelScope.launch {
            currencyRepository.toggleFavourite(currency.symbol, currency.isFavourite)
        }
    }

    fun refresh() {
        updateCurrencies().collectStatus(
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