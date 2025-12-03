package com.mili.eclipsereads.domain.models

import java.util.Date

data class Favorite(
    val favorite_id: Int,
    val user_id: String,
    val book_id: Int,
    val created_at: Date
)
