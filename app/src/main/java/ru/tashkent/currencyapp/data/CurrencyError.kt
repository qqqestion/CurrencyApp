package ru.tashkent.currencyapp.data

sealed interface CurrencyError {
    object InvalidApiKey : CurrencyError
    object NoInternet : CurrencyError
    object UnknownError : CurrencyError
}