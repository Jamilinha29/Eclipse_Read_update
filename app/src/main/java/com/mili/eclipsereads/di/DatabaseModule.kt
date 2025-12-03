package com.mili.eclipsereads.di

import android.content.Context
import androidx.room.Room
import com.mili.eclipsereads.data.local.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "eclipse-reads-db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideBooksDao(appDatabase: AppDatabase) = appDatabase.booksDao()

    @Provides
    @Singleton
    fun provideReviewsDao(appDatabase: AppDatabase) = appDatabase.reviewsDao()

    @Provides
    @Singleton
    fun provideProfilesDao(appDatabase: AppDatabase) = appDatabase.profilesDao()

    @Provides
    @Singleton
    fun provideReadingDao(appDatabase: AppDatabase) = appDatabase.readingDao()

    @Provides
    @Singleton
    fun provideFavoritesDao(appDatabase: AppDatabase) = appDatabase.favoritesDao()

    @Provides
    @Singleton
    fun provideReadingProgressDao(appDatabase: AppDatabase) = appDatabase.readingProgressDao()
}
