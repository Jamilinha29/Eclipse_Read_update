package com.mili.eclipsereads.domain.models

import java.util.Date

data class Books(
    val book_id: Int,
    val title: String,
    val author: String,
    val cover: String? = null,
    val synopsis: String,
    val book_file_path: String? = null, // Path to the book file in Supabase Storage
    val created_at: Date
)
