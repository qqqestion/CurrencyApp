package ru.tashkent.currencyapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CurrencyEntity::class], version = AppDatabase.DATABASE_VERSION)
abstract class AppDatabase : RoomDatabase() {
    abstract fun currencyDao(): CurrencyDao

    companion object {
        const val DATABASE_NAME = "currency-database"
        const val DATABASE_VERSION = 3
    }
}