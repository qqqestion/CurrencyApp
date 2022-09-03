package ru.tashkent.currencyapp.ui.common

import ru.tashkent.currencyapp.data.CurrencyError

sealed class InvokeStatus {
    object InvokeStarted : InvokeStatus()
    object InvokeSuccess : InvokeStatus()
    data class InvokeError(val error: CurrencyError) : InvokeStatus()
}