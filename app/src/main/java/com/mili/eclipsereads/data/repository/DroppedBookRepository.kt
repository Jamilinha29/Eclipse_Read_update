package com.mili.eclipsereads.data.repository

import com.mili.eclipsereads.data.local.dao.DroppedBookDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DroppedBookRepository @Inject constructor(
    private val droppedBookDao: DroppedBookDao
) {
    fun getDroppedCountForUser(userId: String): Flow<Int> {
        return droppedBookDao.getDroppedCountForUser(userId)
    }
}
