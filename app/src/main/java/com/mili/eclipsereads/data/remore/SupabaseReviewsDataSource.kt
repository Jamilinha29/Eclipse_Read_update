package com.mili.eclipsereads.data.remore

import com.mili.eclipsereads.domain.models.Review
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import javax.inject.Inject

class SupabaseReviewsDataSource @Inject constructor(
    private val postgrest: Postgrest
) {
    suspend fun getReviewsForBook(bookId: Int): List<Review> {
        return postgrest.from("reviews").select { filter {
            eq("book_id", bookId)
        } }.decodeList<Review>()
    }

    suspend fun addReview(review: Review) {
        postgrest.from("reviews").insert(review)
    }

    suspend fun updateReview(reviewId: String, rating: Int, reviewText: String) {
        postgrest.from("reviews").update({ "rating" to rating; "review_text" to reviewText }) { filter {
            eq("review_id", reviewId)
        } }
    }

    suspend fun deleteReview(reviewId: String) {
        postgrest.from("reviews").delete { filter {
            eq("review_id", reviewId)
        } }
    }
}
