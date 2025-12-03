package com.mili.eclipsereads.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import com.mili.eclipsereads.data.repository.AuthRepository
import com.mili.eclipsereads.data.repository.ReadingRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncReadingWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val readingRepository: ReadingRepository,
    private val authRepository: AuthRepository
) : SyncWorker(context, workerParams) {

    override suspend fun doSync() {
        authRepository.getCurrentUserId()?.let {
            readingRepository.refreshReadings(it)
        }
    }
}
