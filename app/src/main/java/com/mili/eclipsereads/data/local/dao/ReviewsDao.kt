package com.mili.eclipsereads.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mili.eclipsereads.data.local.entities.ReviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(review: ReviewEntity)

    @Query("SELECT * FROM reviews WHERE book_id = :bookId")
    fun getReviewsForBook(bookId: Int): Flow<List<ReviewEntity>>

    @Query("DELETE FROM reviews WHERE review_id = :reviewId")
    suspend fun deleteReview(reviewId: String)

    @Query("UPDATE reviews SET rating = :rating, review_text = :reviewText WHERE review_id = :reviewId")
    suspend fun updateReview(reviewId: String, rating: Int, reviewText: String)

    @Query("DELETE FROM reviews WHERE book_id = :bookId")
    suspend fun deleteAllForBook(bookId: Int)
}
