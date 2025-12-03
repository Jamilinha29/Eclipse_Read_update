package com.mili.eclipsereads.data.repository

import com.mili.eclipsereads.data.local.dao.ReviewsDao
import com.mili.eclipsereads.data.local.entities.toDomainModel
import com.mili.eclipsereads.data.local.entities.toEntity
import com.mili.eclipsereads.data.remore.SupabaseReviewsDataSource
import com.mili.eclipsereads.domain.models.Review
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ReviewsRepository @Inject constructor(
    private val reviewsDao: ReviewsDao,
    private val reviewsDataSource: SupabaseReviewsDataSource
) {

    fun getReviewsForBook(bookId: Int): Flow<List<Review>> {
        return reviewsDao.getReviewsForBook(bookId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    suspend fun refreshReviews(bookId: Int) {
        val reviews = reviewsDataSource.getReviewsForBook(bookId)
        reviews.forEach { reviewsDao.insert(it.toEntity()) }
    }
}
