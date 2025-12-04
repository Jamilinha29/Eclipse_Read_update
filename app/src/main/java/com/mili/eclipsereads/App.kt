package com.mili.eclipsereads

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.ListenableWorker
import com.mili.eclipsereads.workers.SyncBooksWorker
import com.mili.eclipsereads.workers.SyncFavoritesWorker
import com.mili.eclipsereads.workers.SyncReadingWorker
import com.mili.eclipsereads.workers.SyncReviewsWorker
import dagger.hilt.android.HiltAndroidApp
import io.sentry.Sentry
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        setupLogging()
        setupRecurringWork()
    }

    private fun setupLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(SentryTree())
        }
    }

    private fun setupRecurringWork() {
        val workManager = WorkManager.getInstance(this)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .setRequiresCharging(true)
            .build()

        scheduleSyncWorker<SyncBooksWorker>(workManager, "sync-books-worker", constraints)
        scheduleSyncWorker<SyncFavoritesWorker>(workManager, "sync-favorites-worker", constraints)
        scheduleSyncWorker<SyncReadingWorker>(workManager, "sync-reading-worker", constraints)
        scheduleSyncWorker<SyncReviewsWorker>(workManager, "sync-reviews-worker", constraints)
    }

    private inline fun <reified T : ListenableWorker> scheduleSyncWorker(
        workManager: WorkManager,
        uniqueWorkName: String,
        constraints: Constraints
    ) {
        val repeatingRequest = PeriodicWorkRequestBuilder<T>(6, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            uniqueWorkName,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest
        )
    }
}

class SentryTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Timber.ERROR || priority == Timber.WARN) {
            Sentry.captureException(t ?: Throwable(message))
        }
    }
}
