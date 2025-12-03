package com.mili.eclipsereads.domain.models

import java.util.Date

data class Review(
    val review_id: Int,
    val book_id: Int,
    val user_id: String,
    val rating: Int,
    val comment: String? = null,
    val created_at: Date
)
