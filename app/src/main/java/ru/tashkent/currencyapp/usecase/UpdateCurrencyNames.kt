package ru.tashkent.currencyapp.usecase

import arrow.core.Either
import kotlinx.coroutines.flow.flow
import ru.tashkent.currencyapp.data.repository.CurrencyRepository
import ru.tashkent.currencyapp.ui.common.InvokeStatus
import javax.inject.Inject

class UpdateCurrencyNames @Inject constructor(
    private val currencyRepository: CurrencyRepository
) {
    operator fun invoke() = flow {
        emit(InvokeStatus.InvokeStarted)
        when (val either = currencyRepository.updateCurrencyNames()) {
            is Either.Left -> emit(InvokeStatus.InvokeError(either.value))
            is Either.Right -> emit(InvokeStatus.InvokeSuccess)
        }
    }
}