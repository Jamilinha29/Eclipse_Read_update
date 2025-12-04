package com.mili.eclipsereads.data.remore

import com.mili.eclipsereads.domain.models.Reading
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import javax.inject.Inject

class SupabaseReadingDataSource @Inject constructor(
    private val postgrest: Postgrest
) {
    suspend fun getReadingsForUser(userId: String): List<Reading> {
        return postgrest.from("readings").select { filter {
            eq("user_id", userId)
        } }.decodeList<Reading>()
    }

    suspend fun addReading(reading: Reading) {
        postgrest.from("readings").insert(reading)
    }

    suspend fun removeReading(userId: String, bookId: Int) {
        postgrest.from("readings").delete { filter {
            eq("user_id", userId)
            eq("book_id", bookId)
        } }
    }

    suspend fun updateReadingProgress(userId: String, bookId: Int, progress: Int) {
        postgrest.from("readings").update({ "progress" to progress }) { filter {
            eq("user_id", userId)
            eq("book_id", bookId)
        } }
    }
}
