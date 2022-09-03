package ru.tashkent.currencyapp.ui.common

import ru.tashkent.currencyapp.R
import ru.tashkent.currencyapp.data.CurrencyError

sealed class UiError {
    data class SimpleError(val message: String) : UiError()
    data class ResourceError(val messageId: Int) : UiError()
}

fun ResourceError(error: CurrencyError): UiError.ResourceError {
    val message = when (error) {
        CurrencyError.InvalidApiKey -> R.string.error_invalid_api_key
        CurrencyError.NoInternet -> R.string.error_no_internet
        CurrencyError.UnknownError -> R.string.error_unknown_error
    }
    return UiError.ResourceError(message)
}
