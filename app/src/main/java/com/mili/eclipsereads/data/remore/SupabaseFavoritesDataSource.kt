package com.mili.eclipsereads.data.remore

import com.mili.eclipsereads.domain.models.Favorite
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import javax.inject.Inject

class SupabaseFavoritesDataSource @Inject constructor(
    private val postgrest: Postgrest
) {
    suspend fun getFavoritesForUser(userId: String): List<Favorite> {
        return postgrest.from("favorites").select { filter {
            eq("user_id", userId)
        } }.decodeList<Favorite>()
    }

    suspend fun addFavorite(favorite: Favorite) {
        postgrest.from("favorites").insert(favorite)
    }

    suspend fun removeFavorite(userId: String, bookId: Int) {
        postgrest.from("favorites").delete { filter {
            eq("user_id", userId)
            eq("book_id", bookId)
        } }
    }
}
