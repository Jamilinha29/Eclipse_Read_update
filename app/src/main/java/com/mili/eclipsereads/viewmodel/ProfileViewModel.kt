package com.mili.eclipsereads.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mili.eclipsereads.data.repository.AuthRepository
import com.mili.eclipsereads.data.repository.ProfilesRepository
import com.mili.eclipsereads.domain.models.Profile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ProfileUiState {
    data class Success(val profile: Profile) : ProfileUiState
    data class Error(val exception: Throwable) : ProfileUiState
    object Loading : ProfileUiState
    object NoProfile : ProfileUiState
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profilesRepository: ProfilesRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

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

            profilesRepository.getProfile(userId)
                .catch { _uiState.value = ProfileUiState.Error(it) }
                .collect { profile ->
                    if (profile != null) {
                        _uiState.value = ProfileUiState.Success(profile)
                    } else {
                        _uiState.value = ProfileUiState.NoProfile
                    }
                }
        }
    }

    fun updateProfile(fullName: String) {
        viewModelScope.launch {
            val currentProfile = (uiState.value as? ProfileUiState.Success)?.profile
            if (currentProfile != null) {
                val updatedProfile = currentProfile.copy(fullName = fullName)
                try {
                    profilesRepository.updateProfile(updatedProfile)
                    _uiState.value = ProfileUiState.Success(updatedProfile)
                } catch (e: Exception) {
                    _uiState.value = ProfileUiState.Error(e)
                }
            }
        }
    }

    fun refreshProfile() {
        getProfile()
    }
}
