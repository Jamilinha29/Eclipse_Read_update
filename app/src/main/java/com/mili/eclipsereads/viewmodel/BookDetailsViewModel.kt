package com.mili.eclipsereads.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mili.eclipsereads.data.repository.BooksRepository
import com.mili.eclipsereads.data.repository.FavoritesRepository
import com.mili.eclipsereads.data.repository.AuthRepository
import com.mili.eclipsereads.domain.models.Favorite
import java.util.Date
import com.mili.eclipsereads.domain.models.Books
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface BookDetailsUiState {
    data class Success(val books: Books) : BookDetailsUiState
    data class Error(val exception: Throwable) : BookDetailsUiState
    object Loading : BookDetailsUiState
    object BookNotFound : BookDetailsUiState
}

@HiltViewModel
class BookDetailsViewModel @Inject constructor(
    private val booksRepository: BooksRepository,
    private val favoritesRepository: FavoritesRepository,
    private val authRepository: AuthRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val bookId: Int = checkNotNull(savedStateHandle["bookId"])

    private val _uiState = MutableStateFlow<BookDetailsUiState>(BookDetailsUiState.Loading)
    val uiState: StateFlow<BookDetailsUiState> = _uiState.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    init {
        getBookDetails()
        observeFavoriteState()
    }

    private fun getBookDetails() {
        viewModelScope.launch {
            _uiState.value = BookDetailsUiState.Loading
            booksRepository.getBookById(bookId)
                .catch { _uiState.value = BookDetailsUiState.Error(it) }
                .collect { book ->
                    if (book != null) {
                        _uiState.value = BookDetailsUiState.Success(book)
                    } else {
                        _uiState.value = BookDetailsUiState.BookNotFound
                    }
                }
        }
    }

    private fun observeFavoriteState() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId() ?: return@launch
            // Keep collecting favorites for the current user and update local flag
            favoritesRepository.getFavoritesForUser(userId)
                .catch { /* ignore errors for state tracking */ }
                .collect { favs ->
                    _isFavorite.value = favs.any { it.book_id == bookId }
                }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId() ?: return@launch
            try {
                if (_isFavorite.value) {
                    favoritesRepository.removeFavorite(userId, bookId)
                } else {
                    val favorite = Favorite(
                        favorite_id = 0,
                        user_id = userId,
                        book_id = bookId,
                        created_at = Date()
                    )
                    favoritesRepository.addFavorite(favorite)
                }
                // Optimistically flip state; the collector on the flow will correct if needed
                _isFavorite.value = !_isFavorite.value
            } catch (e: Exception) {
                _uiState.value = BookDetailsUiState.Error(e)
            }
        }
    }
}
