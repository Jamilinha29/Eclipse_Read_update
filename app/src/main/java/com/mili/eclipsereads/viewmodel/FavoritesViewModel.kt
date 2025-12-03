package com.mili.eclipsereads.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mili.eclipsereads.data.repository.AuthRepository
import com.mili.eclipsereads.data.repository.FavoritesRepository
import com.mili.eclipsereads.domain.models.Favorite
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface FavoritesUiState {
    data class Success(val favorites: List<Favorite>) : FavoritesUiState
    data class Error(val exception: Throwable) : FavoritesUiState
    object Loading : FavoritesUiState
}

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<FavoritesUiState>(FavoritesUiState.Loading)
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        getFavorites()
    }

    private fun getFavorites() {
        viewModelScope.launch {
            _uiState.value = FavoritesUiState.Loading
            val userId = authRepository.getCurrentUserId()
            if (userId == null) {
                _uiState.value = FavoritesUiState.Error(Exception("User not logged in"))
                return@launch
            }

            try {
                favoritesRepository.refreshFavorites(userId)
            } catch (e: Exception) {
                _uiState.value = FavoritesUiState.Error(e)
            }

            favoritesRepository.getFavoritesForUser(userId)
                .catch { _uiState.value = FavoritesUiState.Error(it) }
                .collect { _uiState.value = FavoritesUiState.Success(it) }
        }
    }

    fun refreshFavorites() {
        getFavorites()
    }
}
