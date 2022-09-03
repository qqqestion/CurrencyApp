package ru.tashkent.currencyapp.ui.popular

import ru.tashkent.currencyapp.ui.common.CurrencyRateUi
import ru.tashkent.currencyapp.ui.common.UiError

data class PopularState(
    val currencies: List<CurrencyRateUi> = emptyList(),
    val isLoading: Boolean = false,
    val error: UiError? = null
) {
    override fun toString(): String {
        return "PopularState(currencies = ${currencies.size}, isLoading = $isLoading, error = $error)"
    }
}
