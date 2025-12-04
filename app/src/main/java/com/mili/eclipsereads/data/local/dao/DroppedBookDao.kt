package com.mili.eclipsereads.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mili.eclipsereads.data.local.entities.DroppedBookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DroppedBookDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE) // Ignora se o usuário já dropou este livro antes
    suspend fun insert(droppedBook: DroppedBookEntity)

    @Query("SELECT COUNT(DISTINCT book_id) FROM dropped_books WHERE user_id = :userId")
    fun getDroppedCountForUser(userId: String): Flow<Int>
}
