package com.mili.eclipsereads.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mili.eclipsereads.domain.models.Books
import java.util.Date

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey
    val book_id: Int,
    val title: String,
    val author: String,
    val cover: String? = null,
    val synopsis: String,
    val book_file_path: String? = null,
    val created_at: Date
)

fun BookEntity.toDomainModel(): Books = Books(
    book_id = book_id,
    title = title,
    author = author,
    cover = cover,
    synopsis = synopsis,
    book_file_path = book_file_path,
    created_at = created_at
)

fun Books.toEntity(): BookEntity = BookEntity(
    book_id = book_id,
    title = title,
    author = author,
    cover = cover,
    synopsis = synopsis,
    book_file_path = book_file_path,
    created_at = created_at
)
