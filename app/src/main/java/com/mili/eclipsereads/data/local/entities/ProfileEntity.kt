package com.mili.eclipsereads.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mili.eclipsereads.domain.models.Profile
import java.util.Date

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey
    val profile_id: String,
    val name: String,
    val created_at: Date
)

fun ProfileEntity.toDomainModel(): Profile = Profile(
    profile_id = profile_id,
    name = name,
    created_at = created_at
)

fun Profile.toEntity(): ProfileEntity = ProfileEntity(
    profile_id = profile_id,
    name = name,
    created_at = created_at
)
