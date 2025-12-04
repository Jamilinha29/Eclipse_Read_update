package com.mili.eclipsereads.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mili.eclipsereads.data.repository.BooksRepository
import com.mili.eclipsereads.data.repository.FavoritesRepository
import com.mili.eclipsereads.data.repository.ReadingRepository
import com.mili.eclipsereads.domain.models.Books
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BookDetailsUiState(
    val book: Books? = null,
    val isFavorite: Boolean = false,
    val isInReadingList: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class BookDetailsViewModel @Inject constructor(
    private val booksRepository: BooksRepository,
    private val favoritesRepository: FavoritesRepository,
    private val readingRepository: ReadingRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookDetailsUiState())
    val uiState: StateFlow<BookDetailsUiState> = _uiState.asStateFlow()

    private val bookId: Int = savedStateHandle.get<Int>("bookId")!!
    private val userId: String = "" // TODO: Obter o ID do usuário logado

    init {
        loadBookDetails()
    }

    private fun loadBookDetails() {
        viewModelScope.launch {
            val bookFlow = booksRepository.getBookById(bookId)
            val favoritesFlow = favoritesRepository.getFavoritesForUser(userId)
            val readingFlow = readingRepository.getReadingsForUser(userId)

            combine(bookFlow, favoritesFlow, readingFlow) { book, favorites, readings ->
                BookDetailsUiState(
                    book = book,
                    isFavorite = favorites.any { it.bookId == bookId },
                    isInReadingList = readings.any { it.bookId == bookId },
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            if (_uiState.value.isFavorite) {
                favoritesRepository.removeFavorite(userId, bookId)
            } else {
                _uiState.value.book?.let { 
                    // TODO: Criar e passar um objeto Favorite completo
                }
            }
        }
    }
    
    // TODO: Implementar add/remove da lista de leitura
}