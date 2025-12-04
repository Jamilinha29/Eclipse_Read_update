package com.mili.eclipsereads.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mili.eclipsereads.data.repository.BooksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SettingsEvent {
    data class ShowToast(val message: String) : SettingsEvent
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val booksRepository: BooksRepository
) : ViewModel() {

    private val _eventFlow = MutableSharedFlow<SettingsEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun clearLocalCache() {
        viewModelScope.launch {
            try {
                // Para simplificar, estamos limpando apenas o cache de livros.
                // Uma implementação completa limparia todos os DAOs relevantes.
                booksRepository.clearAllLocalBooks()
                _eventFlow.emit(SettingsEvent.ShowToast("Cache local limpo com sucesso!"))
            } catch (e: Exception) {
                _eventFlow.emit(SettingsEvent.ShowToast("Falha ao limpar o cache."))
            }
        }
    }
}