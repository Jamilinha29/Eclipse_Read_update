package com.mili.eclipsereads.data.repository

import com.mili.eclipsereads.data.local.dao.ReviewsDao
import com.mili.eclipsereads.data.local.entities.toDomainModel
import com.mili.eclipsereads.data.local.entities.toEntity
import com.mili.eclipsereads.data.remore.SupabaseReviewsDataSource
import com.mili.eclipsereads.domain.models.Review
import com.mili.eclipsereads.utils.NetworkConnectivityObserver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class ReviewsRepository @Inject constructor(
    private val reviewsDao: ReviewsDao,
    private val reviewsDataSource: SupabaseReviewsDataSource,
    private val connectivityObserver: NetworkConnectivityObserver
) {

    fun getReviewsForBook(bookId: Int): Flow<List<Review>> {
        return reviewsDao.getReviewsForBook(bookId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    suspend fun addReview(review: Review) {
        reviewsDao.insert(review.toEntity())
        if (connectivityObserver.isOnline.first()) {
            try {
                reviewsDataSource.addReview(review)
            } catch (e: Exception) {
                Timber.e(e, "Failed to add review to remote source")
            }
        }
    }

    suspend fun updateReview(reviewId: String, rating: Int, reviewText: String) {
        reviewsDao.updateReview(reviewId, rating, reviewText)
        if (connectivityObserver.isOnline.first()) {
            try {
                reviewsDataSource.updateReview(reviewId, rating, reviewText)
            } catch (e: Exception) {
                Timber.e(e, "Failed to update review on remote source")
            }
        }
    }

    suspend fun deleteReview(reviewId: String) {
        reviewsDao.deleteReview(reviewId)
        if (connectivityObserver.isOnline.first()) {
            try {
                reviewsDataSource.deleteReview(reviewId)
            } catch (e: Exception) {
                Timber.e(e, "Failed to delete review from remote source")
            }
        }
    }

    suspend fun refreshReviews(bookId: Int) {
        if (connectivityObserver.isOnline.first()) {
            val reviews = reviewsDataSource.getReviewsForBook(bookId)
            reviewsDao.deleteAllForBook(bookId)
            reviews.forEach { reviewsDao.insert(it.toEntity()) }
        }
    }
}
