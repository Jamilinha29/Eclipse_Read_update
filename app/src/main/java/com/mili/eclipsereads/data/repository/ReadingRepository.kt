package com.mili.eclipsereads.data.repository

import com.mili.eclipsereads.data.local.dao.ReadingDao
import com.mili.eclipsereads.data.local.entities.toDomainModel
import com.mili.eclipsereads.data.local.entities.toEntity
import com.mili.eclipsereads.data.remore.SupabaseReadingDataSource
import com.mili.eclipsereads.domain.models.Reading
import com.mili.eclipsereads.utils.NetworkConnectivityObserver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class ReadingRepository @Inject constructor(
    private val readingDao: ReadingDao,
    private val readingDataSource: SupabaseReadingDataSource,
    private val connectivityObserver: NetworkConnectivityObserver
) {

    fun getReadingsForUser(userId: String): Flow<List<Reading>> {
        return readingDao.getReadingsForUser(userId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    suspend fun addReading(reading: Reading) {
        readingDao.insert(reading.toEntity())
        if (connectivityObserver.isOnline.first()) {
            try {
                readingDataSource.addReading(reading)
            } catch (e: Exception) {
                Timber.e(e, "Failed to add reading to remote source")
            }
        }
    }

    suspend fun removeReading(userId: String, bookId: Int) {
        readingDao.deleteReading(userId, bookId)
        if (connectivityObserver.isOnline.first()) {
            try {
                readingDataSource.removeReading(userId, bookId)
            } catch (e: Exception) {
                Timber.e(e, "Failed to remove reading from remote source")
            }
        }
    }

    suspend fun updateReadingProgress(userId: String, bookId: Int, progress: Int) {
        readingDao.updateReadingProgress(userId, bookId, progress)
        if (connectivityObserver.isOnline.first()) {
            try {
                readingDataSource.updateReadingProgress(userId, bookId, progress)
            } catch (e: Exception) {
                Timber.e(e, "Failed to update reading progress on remote source")
            }
        }
    }

    suspend fun refreshReadings(userId: String) {
        if (connectivityObserver.isOnline.first()) {
            val readings = readingDataSource.getReadingsForUser(userId)
            readingDao.deleteAllForUser(userId)
            readings.forEach { readingDao.insert(it.toEntity()) }
        }
    }
}
