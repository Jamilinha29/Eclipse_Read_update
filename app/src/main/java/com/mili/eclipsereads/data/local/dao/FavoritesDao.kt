package com.mili.eclipsereads.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mili.eclipsereads.data.local.entities.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: FavoriteEntity)

    @Query("SELECT * FROM favorites WHERE user_id = :userId")
    fun getFavoritesForUser(userId: String): Flow<List<FavoriteEntity>>

    @Query("DELETE FROM favorites WHERE user_id = :userId AND book_id = :bookId")
    suspend fun deleteFavorite(userId: String, bookId: Int)

    @Query("DELETE FROM favorites WHERE user_id = :userId")
    suspend fun deleteAllForUser(userId: String)
}
