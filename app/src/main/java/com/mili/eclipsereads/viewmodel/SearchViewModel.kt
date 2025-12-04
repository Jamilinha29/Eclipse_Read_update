package com.mili.eclipsereads.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mili.eclipsereads.data.repository.BooksRepository
import com.mili.eclipsereads.domain.models.Books
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val results: List<Books> = emptyList(),
    val isLoading: Boolean = false,
    val isInitial: Boolean = true // To show initial state before any search
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val booksRepository: BooksRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(300) // Wait for 300ms of no new text
                .flatMapLatest { query ->
                    if (query.isBlank()) {
                        flowOf(SearchUiState(isInitial = true))
                    } else {
                        _uiState.value = _uiState.value.copy(isLoading = true, isInitial = false)
                        booksRepository.searchBooks(query).flatMapLatest { results ->
                            flowOf(SearchUiState(results = results, isLoading = false))
                        }
                    }
                }
                .collect { state ->
                    _uiState.value = state
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
}