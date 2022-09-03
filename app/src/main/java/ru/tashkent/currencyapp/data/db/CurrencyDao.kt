package ru.tashkent.currencyapp.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao {
    @Query("SELECT * FROM currencies ORDER BY symbol ASC")
    fun observeAllAbcAsc(): Flow<List<CurrencyEntity>>

    @Query("SELECT * FROM currencies ORDER BY symbol DESC")
    fun observeAllAbcDesc(): Flow<List<CurrencyEntity>>

    @Query("SELECT * FROM currencies ORDER BY rate ASC")
    fun observeAllRateAsc(): Flow<List<CurrencyEntity>>

    @Query("SELECT * FROM currencies ORDER BY rate DESC")
    fun observeAllRateDesc(): Flow<List<CurrencyEntity>>

    @Query("SELECT * FROM currencies WHERE is_favourite = 1 ORDER BY symbol ASC")
    fun observeFavouriteAbcAsc(): Flow<List<CurrencyEntity>>

    @Query("SELECT * FROM currencies WHERE is_favourite = 1 ORDER BY symbol DESC")
    fun observeFavouriteAbcDesc(): Flow<List<CurrencyEntity>>

    @Query("SELECT * FROM currencies WHERE is_favourite = 1 ORDER BY rate ASC")
    fun observeFavouriteRateAsc(): Flow<List<CurrencyEntity>>

    @Query("SELECT * FROM currencies WHERE is_favourite = 1 ORDER BY rate DESC")
    fun observeFavouriteRateDesc(): Flow<List<CurrencyEntity>>

    @Query("SELECT symbol, name FROM currencies ORDER BY symbol ASC")
    fun observeNames(): Flow<List<UpdateNameEntity>>

    @Query("SELECT COUNT(*) == 0 FROM currencies")
    suspend fun isEmpty(): Boolean

    @Query("SELECT * FROM currencies WHERE is_favourite = 1")
    suspend fun getAllFavourite(): List<CurrencyEntity>

    @Insert(entity = CurrencyEntity::class, onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: UpdateRateEntity)

    @Update(entity = CurrencyEntity::class)
    suspend fun update(entity: UpdateIsFavouriteEntity)

    @Update(entity = CurrencyEntity::class)
    suspend fun update(entity: UpdateRateEntity)

    @Update(entity = CurrencyEntity::class)
    suspend fun updateNames(entities: List<UpdateNameEntity>)
}
