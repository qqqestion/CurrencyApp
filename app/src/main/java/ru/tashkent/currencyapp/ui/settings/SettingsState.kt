package ru.tashkent.currencyapp.ui.settings

import ru.tashkent.currencyapp.ui.common.UiError

data class SettingsState(
    val currencies: List<CurrencyNameUi> = emptyList(),
    val scrollToPosition: Int = -1,
    val isLoading: Boolean = false,
    val error: UiError? = null
)