package ru.tashkent.currencyapp.ui.favourite

import ru.tashkent.currencyapp.ui.common.CurrencyRateUi
import ru.tashkent.currencyapp.ui.common.UiError

data class FavouriteState(
    val currencies: List<CurrencyRateUi> = emptyList(),
    val isLoading: Boolean = false,
    val error: UiError? = null
)
