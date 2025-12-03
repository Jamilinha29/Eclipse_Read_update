package com.mili.eclipsereads.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mili.eclipsereads.data.local.entities.ReadingProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(readingProgress: ReadingProgressEntity)

    @Query("SELECT * FROM reading_progress WHERE reading_id = :readingId")
    fun getReadingProgressForReading(readingId: Int): Flow<List<ReadingProgressEntity>>
}
