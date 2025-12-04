package com.mili.eclipsereads.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.mili.eclipsereads.data.local.db.AppDatabase
import com.mili.eclipsereads.data.local.entities.toDomainModel
import com.mili.eclipsereads.data.paging.BooksRemoteMediator
import com.mili.eclipsereads.data.remore.SupabaseBooksDataSource
import com.mili.eclipsereads.domain.models.Books
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class BooksRepository @Inject constructor(
    private val database: AppDatabase,
    private val booksDataSource: SupabaseBooksDataSource
) {

    fun getBooksPaged(): Flow<PagingData<Books>> {
        val booksDao = database.booksDao()
        return Pager(
            config = PagingConfig(pageSize = 20),
            remoteMediator = BooksRemoteMediator(booksDataSource, database),
            pagingSourceFactory = { booksDao.pagingSource() }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomainModel() }
        }
    }

    fun getBookById(bookId: Int): Flow<Books?> {
        val booksDao = database.booksDao()
        return booksDao.getBookById(bookId).map { it?.toDomainModel() }
    }

    fun getBooksByIds(bookIds: List<Int>): Flow<List<Books>> {
        val booksDao = database.booksDao()
        return booksDao.getBooksByIds(bookIds).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    fun searchBooks(query: String): Flow<List<Books>> {
        val booksDao = database.booksDao()
        return booksDao.searchBooks(query).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    suspend fun clearAllLocalBooks() {
        database.booksDao().clearAll()
    }
}
