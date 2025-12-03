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
}
