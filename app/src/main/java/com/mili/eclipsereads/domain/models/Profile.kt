package com.mili.eclipsereads.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: String,
    val fullName: String,
    val email: String? = null,
    val avatarUrl: String? = null,
    val createdAt: String
)
