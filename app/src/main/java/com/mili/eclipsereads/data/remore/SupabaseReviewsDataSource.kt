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
}
