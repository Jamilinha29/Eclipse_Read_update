package com.mili.eclipsereads.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mili.eclipsereads.domain.models.Reading
import kotlinx.datetime.Instant
import java.util.Date

@Entity(tableName = "readings")
data class ReadingEntity(
    @PrimaryKey
    val reading_id: Int,
    val user_id: String,
    val book_id: Int,
    val start_date: Date,
    val end_date: Date? = null,
    val created_at: Date
)

fun ReadingEntity.toDomainModel(): Reading = Reading(
    reading_id = reading_id,
    user_id = user_id,
    book_id = book_id,
    start_date = Instant.fromEpochMilliseconds(start_date.time),
    end_date = end_date?.let { Instant.fromEpochMilliseconds(it.time) },
    created_at = Instant.fromEpochMilliseconds(created_at.time)
)

fun Reading.toEntity(): ReadingEntity = ReadingEntity(
    reading_id = reading_id,
    user_id = user_id,
    book_id = book_id,
    start_date = Date(start_date.toEpochMilliseconds()),
    end_date = end_date?.let { Date(it.toEpochMilliseconds()) },
    created_at = Date(created_at.toEpochMilliseconds())
)
