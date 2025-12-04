package com.mili.eclipsereads.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mili.eclipsereads.data.repository.AuthRepository
import com.mili.eclipsereads.data.repository.DroppedBookRepository
import com.mili.eclipsereads.data.repository.FavoritesRepository
import com.mili.eclipsereads.data.repository.ProfilesRepository
import com.mili.eclipsereads.data.repository.ReadingRepository
import com.mili.eclipsereads.domain.models.Profile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ProfileUiState {
    data class Success(
        val profile: Profile,
        val favoritesCount: Int = 0,
        val readingCount: Int = 0,
        val droppedCount: Int = 0
    ) : ProfileUiState

    data class Error(val exception: Throwable) : ProfileUiState
    object Loading : ProfileUiState
    object NoProfile : ProfileUiState
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profilesRepository: ProfilesRepository,
    private val authRepository: AuthRepository,
    private val favoritesRepository: FavoritesRepository,
    private val readingRepository: ReadingRepository,
    private val droppedBookRepository: DroppedBookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<Unit>()
    val navigationEvent: SharedFlow<Unit> = _navigationEvent.asSharedFlow()

    init {
        getProfile()
    }

    private fun getProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            val userId = authRepository.getCurrentUserId()
            if (userId == null) {
                _uiState.value = ProfileUiState.Error(Exception("User not logged in"))
                return@launch
            }

            try {
                profilesRepository.refreshProfile(userId)
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e)
            }

            val profileFlow = profilesRepository.getProfile(userId)
            val favoritesFlow = favoritesRepository.getFavoritesForUser(userId)
            val readingFlow = readingRepository.getReadingsForUser(userId)
            val droppedFlow = droppedBookRepository.getDroppedCountForUser(userId)

            combine(profileFlow, favoritesFlow, readingFlow, droppedFlow) { profile, favorites, reading, droppedCount ->
                if (profile != null) {
                    ProfileUiState.Success(
                        profile = profile,
                        favoritesCount = favorites.size,
                        readingCount = reading.size,
                        droppedCount = droppedCount
                    )
                } else {
                    ProfileUiState.NoProfile
                }
            }
            .catch { _uiState.value = ProfileUiState.Error(it) }
            .collect { _uiState.value = it }
        }
    }

    fun updateProfileImage(uri: Uri) {
        viewModelScope.launch {
            try {
                val userId = authRepository.getCurrentUserId() ?: return@launch
                profilesRepository.uploadAvatar(userId, uri)
                refreshProfile()
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e)
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _navigationEvent.emit(Unit)
        }
    }

    fun refreshProfile() {
        getProfile()
    }
}
