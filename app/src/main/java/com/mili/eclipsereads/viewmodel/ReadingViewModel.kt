package com.mili.eclipsereads.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mili.eclipsereads.data.repository.AuthRepository
import com.mili.eclipsereads.data.repository.ReadingRepository
import com.mili.eclipsereads.domain.models.Reading
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ReadingUiState {
    data class Success(val readings: List<Reading>) : ReadingUiState
    data class Error(val exception: Throwable) : ReadingUiState
    object Loading : ReadingUiState
}

@HiltViewModel
class ReadingViewModel @Inject constructor(
    private val readingRepository: ReadingRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReadingUiState>(ReadingUiState.Loading)
    val uiState: StateFlow<ReadingUiState> = _uiState.asStateFlow()

    init {
        getReadings()
    }

    private fun getReadings() {
        viewModelScope.launch {
            _uiState.value = ReadingUiState.Loading
            val userId = authRepository.getCurrentUserId()
            if (userId == null) {
                _uiState.value = ReadingUiState.Error(Exception("User not logged in"))
                return@launch
            }

            try {
                readingRepository.refreshReadings(userId)
            } catch (e: Exception) {
                _uiState.value = ReadingUiState.Error(e)
            }

            readingRepository.getReadingsForUser(userId)
                .catch { _uiState.value = ReadingUiState.Error(it) }
                .collect { _uiState.value = ReadingUiState.Success(it) }
        }
    }

    fun refreshReadings() {
        getReadings()
    }
}
