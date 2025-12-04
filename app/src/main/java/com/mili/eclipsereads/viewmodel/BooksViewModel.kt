package com.mili.eclipsereads.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mili.eclipsereads.data.repository.BooksRepository
import com.mili.eclipsereads.domain.models.Books
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class BooksViewModel @Inject constructor(
    private val booksRepository: BooksRepository
) : ViewModel() {

    val books: Flow<PagingData<Books>> = booksRepository.getBooksPaged()
        .cachedIn(viewModelScope)

}
