package com.mili.eclipsereads.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.mili.eclipsereads.data.local.db.AppDatabase
import com.mili.eclipsereads.data.local.entities.BookEntity
import com.mili.eclipsereads.data.local.entities.toEntity
import com.mili.eclipsereads.data.remore.SupabaseBooksDataSource
import java.io.IOException
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class BooksRemoteMediator @Inject constructor(
    private val booksDataSource: SupabaseBooksDataSource,
    private val database: AppDatabase
) : RemoteMediator<Int, BookEntity>() {

    private val booksDao = database.booksDao()

    override suspend fun load(loadType: LoadType, state: PagingState<Int, BookEntity>): MediatorResult {
        return try {
            // For this simple example, we'll always refresh from the network.
            // A real-world implementation would handle pagination with the remote source.
            if (loadType == LoadType.REFRESH) {
                val books = booksDataSource.getBooks()
                database.withTransaction {
                    booksDao.clearAll()
                    booksDao.insertAll(books.map { it.toEntity() })
                }
            }
            MediatorResult.Success(endOfPaginationReached = true)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}