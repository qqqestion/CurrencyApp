package ru.tashkent.currencyapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
class DispatchersModule {
    @[Provides MainDispatcher]
    fun provideMainDispatcher() = Dispatchers.Main

    @[Provides IoDispatcher]
    fun provideIoDispatcher() = Dispatchers.IO

    @[Provides DefaultDispatcher]
    fun provideDefaultDispatcher() = Dispatchers.Default
}

@Qualifier
annotation class MainDispatcher

@Qualifier
annotation class IoDispatcher

@Qualifier
annotation class DefaultDispatcher
