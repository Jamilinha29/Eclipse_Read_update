package com.mili.eclipsereads.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mili.eclipsereads.data.repository.AuthRepository
import com.mili.eclipsereads.data.repository.BooksRepository
import com.mili.eclipsereads.data.repository.FavoritesRepository
import com.mili.eclipsereads.data.repository.ReadingRepository
import com.mili.eclipsereads.domain.models.Books
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LibraryUiState(
    val favorites: List<Books> = emptyList(),
    val reading: List<Books> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
    private val readingRepository: ReadingRepository,
    private val booksRepository: BooksRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init {
        loadLibraryContent()
    }

    private fun loadLibraryContent() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId()
            if (userId == null) {
                // Handle user not logged in
                _uiState.value = LibraryUiState(isLoading = false)
                return@launch
            }

            val favoritesFlow = favoritesRepository.getFavoritesForUser(userId)
                .flatMapLatest { favorites ->
                    val bookIds = favorites.map { it.bookId }
                    if (bookIds.isNotEmpty()) {
                        booksRepository.getBooksByIds(bookIds)
                    } else {
                        flowOf(emptyList())
                    }
                }

            val readingFlow = readingRepository.getReadingsForUser(userId)
                .flatMapLatest { readings ->
                    val bookIds = readings.map {it.bookId }
                    if (bookIds.isNotEmpty()) {
                        booksRepository.getBooksByIds(bookIds)
                    } else {
                        flowOf(emptyList())
                    }
                }

            combine(favoritesFlow, readingFlow) { favorites, reading ->
                LibraryUiState(
                    favorites = favorites,
                    reading = reading,
                    isLoading = false
                )
            }.collect {
                _uiState.value = it
            }
        }
    }
}