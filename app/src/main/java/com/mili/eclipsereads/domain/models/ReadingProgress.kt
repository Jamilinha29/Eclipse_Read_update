package com.mili.eclipsereads.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class ReadingProgress(
    val progressId: Int,
    val readingId: Int,
    val page: Int,
    val createdAt: String
)
