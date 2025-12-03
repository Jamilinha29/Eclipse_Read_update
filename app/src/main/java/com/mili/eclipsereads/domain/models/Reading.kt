package com.mili.eclipsereads.domain.models

import java.util.Date

data class Reading(
    val reading_id: Int,
    val user_id: String,
    val book_id: Int,
    val start_date: Date,
    val end_date: Date? = null,
    val created_at: Date
)
