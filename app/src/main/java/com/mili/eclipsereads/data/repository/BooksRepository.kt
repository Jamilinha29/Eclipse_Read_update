package com.mili.eclipsereads.data.repository

import com.mili.eclipsereads.data.local.dao.BooksDao
import com.mili.eclipsereads.data.local.entities.toDomainModel
import com.mili.eclipsereads.data.local.entities.toEntity
import com.mili.eclipsereads.data.remore.SupabaseBooksDataSource
import com.mili.eclipsereads.domain.models.Books
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BooksRepository @Inject constructor(
    private val booksDao: BooksDao,
    private val booksDataSource: SupabaseBooksDataSource
) {

    fun getBooks(): Flow<List<Books>> {
        return booksDao.getAll().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    fun getBookById(bookId: Int): Flow<Books?> {
        return booksDao.getBookById(bookId).map { it?.toDomainModel() }
    }

    suspend fun refreshBooks() {
        val books = booksDataSource.getBooks()
        books.forEach { booksDao.insert(it.toEntity()) }
    }
}
