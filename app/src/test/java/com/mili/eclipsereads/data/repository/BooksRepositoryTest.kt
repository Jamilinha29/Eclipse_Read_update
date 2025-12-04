package com.mili.eclipsereads.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.mili.eclipsereads.data.local.dao.BooksDao
import com.mili.eclipsereads.data.local.db.AppDatabase
import com.mili.eclipsereads.data.local.entities.BookEntity
import com.mili.eclipsereads.data.remore.SupabaseBooksDataSource
import com.mili.eclipsereads.viewmodel.MainCoroutineRule
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class BooksRepositoryTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var database: AppDatabase
    private lateinit var booksDao: BooksDao
    private lateinit var booksDataSource: SupabaseBooksDataSource
    private lateinit var repository: BooksRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries() // For testing only
            .build()
        booksDao = database.booksDao()
        booksDataSource = mockk()
        repository = BooksRepository(database, booksDataSource)
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        database.close()
    }

    @Test
    fun `getBookById should return correct book from local database`() = runTest {
        // Arrange
        val bookEntity = BookEntity(id = 1, title = "The Lord of the Rings", author = "J.R.R. Tolkien", coverUrl = "url", description = "desc", genre = "Fantasy")
        booksDao.insert(bookEntity)

        // Act
        val result = repository.getBookById(1).first()

        // Assert
        assertNotNull(result)
        assertEquals("The Lord of the Rings", result?.title)
    }
}