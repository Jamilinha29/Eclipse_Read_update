package com.mili.eclipsereads.data.repository

import com.mili.eclipsereads.data.local.dao.ReadingDao
import com.mili.eclipsereads.data.local.entities.toDomainModel
import com.mili.eclipsereads.data.local.entities.toEntity
import com.mili.eclipsereads.data.remore.SupabaseReadingDataSource
import com.mili.eclipsereads.domain.models.Reading
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ReadingRepository @Inject constructor(
    private val readingDao: ReadingDao,
    private val readingDataSource: SupabaseReadingDataSource
) {

    fun getReadingsForUser(userId: String): Flow<List<Reading>> {
        return readingDao.getReadingsForUser(userId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    suspend fun refreshReadings(userId: String) {
        val readings = readingDataSource.getReadingsForUser(userId)
        readings.forEach { readingDao.insert(it.toEntity()) }
    }
}
