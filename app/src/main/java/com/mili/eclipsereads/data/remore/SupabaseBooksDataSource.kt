package com.mili.eclipsereads.data.remore

import com.mili.eclipsereads.domain.models.Books
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Inject

class SupabaseBooksDataSource @Inject constructor(
    private val postgrest: Postgrest
) {
    suspend fun getBooks(): List<Books> {
        return postgrest.from("books").select().decodeList<Books>()
    }
}
