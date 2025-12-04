package com.mili.eclipsereads.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mili.eclipsereads.domain.models.Review
import kotlinx.datetime.Instant
import java.util.Date

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey
    val review_id: Int,
    val book_id: Int,
    val user_id: String,
    val rating: Int,
    val comment: String? = null,
    val created_at: Date
)

fun ReviewEntity.toDomainModel(): Review = Review(
    review_id = review_id,
    book_id = book_id,
    user_id = user_id,
    rating = rating,
    comment = comment,
    created_at = Instant.fromEpochMilliseconds(created_at.time)
)

fun Review.toEntity(): ReviewEntity = ReviewEntity(
    review_id = review_id,
    book_id = book_id,
    user_id = user_id,
    rating = rating,
    comment = comment,
    created_at = Date(created_at.toEpochMilliseconds())
)
