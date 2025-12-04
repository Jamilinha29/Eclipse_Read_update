package com.mili.eclipsereads.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import com.mili.eclipsereads.data.repository.BooksRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncBooksWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val booksRepository: BooksRepository
) : SyncWorker(context, workerParams) {

    override suspend fun doSync() {
        booksRepository.refreshBooks()
    }
}