package com.mili.eclipsereads.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mili.eclipsereads.domain.models.ReadingProgress
import java.util.Date

@Entity(tableName = "reading_progress")
data class ReadingProgressEntity(
    @PrimaryKey
    val progress_id: Int,
    val reading_id: Int,
    val page: Int,
    val created_at: Date
)

fun ReadingProgressEntity.toDomainModel(): ReadingProgress = ReadingProgress(
    progress_id = progress_id,
    reading_id = reading_id,
    page = page,
    created_at = created_at
)

fun ReadingProgress.toEntity(): ReadingProgressEntity = ReadingProgressEntity(
    progress_id = progress_id,
    reading_id = reading_id,
    page = page,
    created_at = created_at
)
