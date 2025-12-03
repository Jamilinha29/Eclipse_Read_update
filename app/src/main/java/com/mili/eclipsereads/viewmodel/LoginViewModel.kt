package com.mili.eclipsereads.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mili.eclipsereads.data.repository.AuthRepository
import com.mili.eclipsereads.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface LoginUiState {
    object Success : LoginUiState
    data class Error(val exception: Throwable) : LoginUiState
    object Loading : LoginUiState
    object Idle : LoginUiState
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // LOGIN COM GOOGLE -----------------------------------------

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                val token = authRepository.signInWithGoogle(idToken)
                if (token != null) {
                    tokenManager.saveToken(token)
                    _uiState.value = LoginUiState.Success
                } else {
                    _uiState.value = LoginUiState.Error(Exception("Google Sign-In failed"))
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(e)
            }
        }
    }

    // LOGIN COM EMAIL / SENHA ----------------------------------

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                val token = authRepository.signInWithEmail(email, password)
                if (token != null) {
                    tokenManager.saveToken(token)
                    _uiState.value = LoginUiState.Success
                } else {
                    _uiState.value = LoginUiState.Error(
                        Exception("E-mail ou senha inválidos")
                    )
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(e)
            }
        }
    }
}
