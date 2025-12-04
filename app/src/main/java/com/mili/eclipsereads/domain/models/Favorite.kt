package com.mili.eclipsereads.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Favorite(
    val favoriteId: Int,
    val userId: String,
    val bookId: Int,
    val createdAt: String
)
