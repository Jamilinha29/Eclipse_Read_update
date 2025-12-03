package com.mili.eclipsereads.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mili.eclipsereads.data.repository.BooksRepository
import com.mili.eclipsereads.domain.models.Books
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface BooksUiState {
    data class Success(val books: List<Books>) : BooksUiState
    data class Error(val exception: Throwable) : BooksUiState
    object Loading : BooksUiState
}

@HiltViewModel
class BooksViewModel @Inject constructor(
    private val booksRepository: BooksRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BooksUiState>(BooksUiState.Loading)
    val uiState: StateFlow<BooksUiState> = _uiState.asStateFlow()

    init {
        getBooks()
        refreshBooks()
    }

    private fun getBooks() {
        viewModelScope.launch {
            booksRepository.getBooks()
                .catch { _uiState.value = BooksUiState.Error(it) }
                .collect { _uiState.value = BooksUiState.Success(it) }
        }
    }

    fun refreshBooks() {
        viewModelScope.launch {
            try {
                booksRepository.refreshBooks()
            } catch (e: Exception) {
                _uiState.value = BooksUiState.Error(e)
            }
        }
    }
}
