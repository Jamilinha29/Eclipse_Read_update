package com.mili.eclipsereads.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import com.mili.eclipsereads.data.repository.AuthRepository
import com.mili.eclipsereads.data.repository.FavoritesRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncFavoritesWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val favoritesRepository: FavoritesRepository,
    private val authRepository: AuthRepository
) : SyncWorker(context, workerParams) {

    override suspend fun doSync() {
        authRepository.getCurrentUserId()?.let {
            favoritesRepository.refreshFavorites(it)
        }
    }
}
