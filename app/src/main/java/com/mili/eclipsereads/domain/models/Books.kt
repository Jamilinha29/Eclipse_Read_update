package com.mili.eclipsereads.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Books(
    val id: String,
    val title: String,
    val author: String,
    val coverUrl: String? = null,
    val description: String? = null,
    val filePath: String? = null,
    val createdAt: String
)
