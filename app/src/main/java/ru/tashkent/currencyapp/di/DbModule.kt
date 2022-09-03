package ru.tashkent.currencyapp.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.tashkent.currencyapp.data.db.AppDatabase

@Module
@InstallIn(SingletonComponent::class)
class DbModule {
    @Provides
    fun provideDatabase(application: Application) = Room
        .databaseBuilder(application, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun provideCurrencyDao(database: AppDatabase) = database.currencyDao()
}