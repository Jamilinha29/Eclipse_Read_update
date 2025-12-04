package com.mili.eclipsereads.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Review(
    val reviewId: Int,
    val bookId: Int,
    val userId: String,
    val rating: Int,
    val comment: String? = null,
    val createdAt: String
)
