package ru.tashkent.currencyapp.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currencies")
data class CurrencyEntity(
    @PrimaryKey @ColumnInfo(name = "symbol")
    val symbol: String,
    @ColumnInfo(name = "rate")
    val rate: Double,
    @ColumnInfo(name = "is_favourite", defaultValue = "0")
    val isFavourite: Boolean,
    @ColumnInfo(name = "name", defaultValue = "")
    val name: String
)

@Entity
data class UpdateRateEntity(
    @ColumnInfo(name = "symbol")
    val symbol: String,
    @ColumnInfo(name = "rate")
    val rate: Double,
)

@Entity
data class UpdateIsFavouriteEntity(
    @ColumnInfo(name = "symbol")
    val symbol: String,
    @ColumnInfo(name = "is_favourite")
    val isFavourite: Boolean
)

@Entity
data class UpdateNameEntity(
    @ColumnInfo(name = "symbol")
    val symbol: String,
    @ColumnInfo(name = "name")
    val name: String
)
