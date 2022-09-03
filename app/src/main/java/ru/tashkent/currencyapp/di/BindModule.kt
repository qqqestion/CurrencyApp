package ru.tashkent.currencyapp.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.tashkent.currencyapp.data.repository.CurrencyRepository
import ru.tashkent.currencyapp.data.repository.CurrencyRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
interface BindModule {
    @Binds
    fun bindCurrencyRepository(implementation: CurrencyRepositoryImpl): CurrencyRepository
}