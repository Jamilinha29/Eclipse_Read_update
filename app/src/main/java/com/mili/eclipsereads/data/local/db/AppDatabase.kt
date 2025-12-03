package com.mili.eclipsereads.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mili.eclipsereads.data.local.dao.*
import com.mili.eclipsereads.data.local.entities.*
import com.mili.eclipsereads.utils.DateConverter

@Database(
    entities = [
        BookEntity::class,
        ReviewEntity::class,
        ProfileEntity::class,
        ReadingEntity::class,
        FavoriteEntity::class,
        ReadingProgressEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun booksDao(): BooksDao
    abstract fun reviewsDao(): ReviewsDao
    abstract fun profilesDao(): ProfilesDao
    abstract fun readingDao(): ReadingDao
    abstract fun favoritesDao(): FavoritesDao
    abstract fun readingProgressDao(): ReadingProgressDao
}
