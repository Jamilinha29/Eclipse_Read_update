package com.mili.eclipsereads.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mili.eclipsereads.domain.models.Favorite
import java.util.Date

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val favorite_id: Int,
    val user_id: String,
    val book_id: Int,
    val created_at: Date
)

fun FavoriteEntity.toDomainModel(): Favorite = Favorite(
    favorite_id = favorite_id,
    user_id = user_id,
    book_id = book_id,
    created_at = created_at
)

fun Favorite.toEntity(): FavoriteEntity = FavoriteEntity(
    favorite_id = favorite_id,
    user_id = user_id,
    book_id = book_id,
    created_at = created_at
)
