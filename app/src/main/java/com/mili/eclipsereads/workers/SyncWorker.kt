package com.mili.eclipsereads.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
abstract class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    abstract suspend fun doSync()

    override suspend fun doWork(): Result {
        return try {
            doSync()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
