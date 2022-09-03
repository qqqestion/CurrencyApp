package ru.tashkent.currencyapp.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.tashkent.currencyapp.data.repository.CurrencyRepository
import ru.tashkent.currencyapp.ui.common.ErrorManager
import ru.tashkent.currencyapp.ui.common.ResourceError
import ru.tashkent.currencyapp.ui.common.collectStatus
import ru.tashkent.currencyapp.usecase.UpdateCurrencyNames
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val currencyRepository: CurrencyRepository,
    updateCurrencyNames: UpdateCurrencyNames,
    private val errorManager: ErrorManager
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val state = combine(
        currencyRepository.observeCurrencyNames(),
        currencyRepository.observeBaseCurrency(),
        _isLoading,
        errorManager.errors,
    ) { currencies, baseCurrency, isLoading, error ->
        val uiCurrencies =
            currencies.map { CurrencyNameUi(it.symbol, it.name, it.symbol == baseCurrency) }
        SettingsState(
            currencies = uiCurrencies,
            scrollToPosition = uiCurrencies.indexOfFirst { it.isSelected },
            isLoading = isLoading,
            error = error
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(2000),
        SettingsState(isLoading = true)
    )

    init {
        updateCurrencyNames().collectStatus(
            scope = viewModelScope,
            onStarted = { _isLoading.value = true },
            onSuccess = { _isLoading.value = false },
            onError = { error ->
                errorManager.addError(ResourceError(error))
                _isLoading.value = false
            }
        )
    }

    fun selectCurrency(currency: CurrencyNameUi) {
        viewModelScope.launch {
            currencyRepository.setBaseCurrency(currency.symbol)
        }
    }

    fun dismissError() {
        errorManager.dismissError()
    }
}