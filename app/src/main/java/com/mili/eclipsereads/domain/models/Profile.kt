package com.mili.eclipsereads.domain.models

import java.util.Date

data class Profile(
    val profile_id: String,
    val name: String,
    val created_at: Date
)
