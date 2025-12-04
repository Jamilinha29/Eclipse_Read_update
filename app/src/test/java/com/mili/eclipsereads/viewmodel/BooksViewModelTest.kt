package com.mili.eclipsereads.viewmodel

import androidx.paging.PagingData
import app.cash.turbine.test
import com.mili.eclipsereads.data.repository.BooksRepository
import com.mili.eclipsereads.domain.models.Books
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@ExperimentalCoroutinesApi
class MainCoroutineRule(private val dispatcher: StandardTestDispatcher = StandardTestDispatcher()) : TestWatcher() {
    override fun starting(description: Description) {
        super.starting(description)
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        super.finished(description)
        Dispatchers.resetMain()
    }
}

class BooksViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var booksRepository: BooksRepository
    private lateinit var viewModel: BooksViewModel

    @Before
    fun setUp() {
        booksRepository = mockk()
    }

    @Test
    fun `initialization should expose a non-null flow of books`() = runTest {
        // Arrange
        val expectedPagingData = PagingData.from(listOf<Books>())
        every { booksRepository.getBooksPaged() } returns flowOf(expectedPagingData)

        // Act
        viewModel = BooksViewModel(booksRepository)

        // Assert
        assertNotNull(viewModel.books)
        viewModel.books.test {
            val emission = awaitItem()
            assertNotNull(emission)
            cancelAndIgnoreRemainingEvents()
        }
    }
}