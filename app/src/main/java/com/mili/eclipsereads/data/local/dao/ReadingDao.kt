package com.mili.eclipsereads.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mili.eclipsereads.data.local.entities.ReadingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reading: ReadingEntity)

    @Query("SELECT * FROM readings WHERE user_id = :userId")
    fun getReadingsForUser(userId: String): Flow<List<ReadingEntity>>

    @Query("DELETE FROM readings WHERE user_id = :userId AND book_id = :bookId")
    suspend fun deleteReading(userId: String, bookId: Int)

    @Query("UPDATE readings SET progress = :progress WHERE user_id = :userId AND book_id = :bookId")
    suspend fun updateReadingProgress(userId: String, bookId: Int, progress: Int)

    @Query("DELETE FROM readings WHERE user_id = :userId")
    suspend fun deleteAllForUser(userId: String)
}
