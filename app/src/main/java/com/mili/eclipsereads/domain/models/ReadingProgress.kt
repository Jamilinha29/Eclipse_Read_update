package com.mili.eclipsereads.domain.models

import java.util.Date

data class ReadingProgress(
    val progress_id: Int,
    val reading_id: Int,
    val page: Int,
    val created_at: Date
)
