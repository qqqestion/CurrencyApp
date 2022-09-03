package ru.tashkent.currencyapp.ui.common

import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@ViewModelScoped
class ErrorManager @Inject constructor() {
    private val _errors = MutableStateFlow<UiError?>(null)
    val errors = _errors.asStateFlow()

    fun addError(error: UiError) {
        _errors.value = error
    }

    fun dismissError() {
        _errors.value = null
    }
}