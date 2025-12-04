package com.mili.eclipsereads.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mili.eclipsereads.data.local.entities.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BooksDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(book: BookEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(books: List<BookEntity>)

    @Query("SELECT * FROM books")
    fun pagingSource(): PagingSource<Int, BookEntity>

    @Query("SELECT * FROM books WHERE id = :bookId")
    fun getBookById(bookId: Int): Flow<BookEntity?>

    @Query("SELECT * FROM books WHERE id IN (:bookIds)")
    fun getBooksByIds(bookIds: List<Int>): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE title LIKE '%' || :query || '%'")
    fun searchBooks(query: String): Flow<List<BookEntity>>

    @Query("DELETE FROM books")
    suspend fun clearAll()
}
