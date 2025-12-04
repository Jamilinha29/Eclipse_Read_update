package com.mili.eclipsereads.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Reading(
    val readingId: Int,
    val userId: String,
    val bookId: Int,
    val startDate: String,
    val endDate: String? = null,
    val createdAt: String
)
