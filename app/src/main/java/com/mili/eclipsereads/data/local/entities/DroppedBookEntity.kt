package com.mili.eclipsereads.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "dropped_books",
    primaryKeys = ["user_id", "book_id"]
)
data class DroppedBookEntity(
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "book_id") val bookId: Int
)
