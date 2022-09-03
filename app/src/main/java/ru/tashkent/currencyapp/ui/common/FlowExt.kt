package ru.tashkent.currencyapp.ui.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ru.tashkent.currencyapp.data.CurrencyError

fun Flow<InvokeStatus>.collectStatus(
    scope: CoroutineScope,
    onStarted: () -> Unit,
    onSuccess: () -> Unit,
    onError: (CurrencyError) -> Unit,
) = scope.launch {
    collect { status ->
        when (status) {
            InvokeStatus.InvokeStarted -> onStarted()
            InvokeStatus.InvokeSuccess -> onSuccess()
            is InvokeStatus.InvokeError -> onError(status.error)
        }
    }
}
