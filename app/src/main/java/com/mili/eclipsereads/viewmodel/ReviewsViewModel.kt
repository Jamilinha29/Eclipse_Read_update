package com.mili.eclipsereads.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mili.eclipsereads.data.repository.ReviewsRepository
import com.mili.eclipsereads.domain.models.Review
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ReviewsUiState {
    data class Success(val reviews: List<Review>) : ReviewsUiState
    data class Error(val exception: Throwable) : ReviewsUiState
    object Loading : ReviewsUiState
}

@HiltViewModel
class ReviewsViewModel @Inject constructor(
    private val reviewsRepository: ReviewsRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val bookId: Int = checkNotNull(savedStateHandle["bookId"])

    private val _uiState = MutableStateFlow<ReviewsUiState>(ReviewsUiState.Loading)
    val uiState: StateFlow<ReviewsUiState> = _uiState.asStateFlow()

    init {
        getReviews()
    }

    private fun getReviews() {
        viewModelScope.launch {
            _uiState.value = ReviewsUiState.Loading
            try {
                reviewsRepository.refreshReviews(bookId)
            } catch (e: Exception) {
                _uiState.value = ReviewsUiState.Error(e)
            }

            reviewsRepository.getReviewsForBook(bookId)
                .catch { _uiState.value = ReviewsUiState.Error(it) }
                .collect { _uiState.value = ReviewsUiState.Success(it) }
        }
    }

    fun refreshReviews() {
        getReviews()
    }
}
