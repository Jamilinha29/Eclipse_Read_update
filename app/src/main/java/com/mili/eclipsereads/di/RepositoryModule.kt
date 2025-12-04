package com.mili.eclipsereads.di

import android.content.Context
import com.mili.eclipsereads.data.local.db.AppDatabase
import com.mili.eclipsereads.data.remore.*
import com.mili.eclipsereads.data.repository.*
import com.mili.eclipsereads.utils.NetworkConnectivityObserver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(authDataSource: SupabaseAuthDataSource): AuthRepository {
        return AuthRepository(authDataSource)
    }

    @Provides
    @Singleton
    fun provideBooksRepository(database: AppDatabase, booksDataSource: SupabaseBooksDataSource): BooksRepository {
        return BooksRepository(database, booksDataSource)
    }

    @Provides
    @Singleton
    fun provideReadingRepository(
        readingDao: com.mili.eclipsereads.data.local.dao.ReadingDao,
        readingDataSource: SupabaseReadingDataSource,
        connectivityObserver: NetworkConnectivityObserver
    ): ReadingRepository {
        return ReadingRepository(readingDao, readingDataSource, connectivityObserver)
    }

    @Provides
    @Singleton
    fun provideReviewsRepository(
        reviewsDao: com.mili.eclipsereads.data.local.dao.ReviewsDao,
        reviewsDataSource: SupabaseReviewsDataSource,
        connectivityObserver: NetworkConnectivityObserver
    ): ReviewsRepository {
        return ReviewsRepository(reviewsDao, reviewsDataSource, connectivityObserver)
    }

    @Provides
    @Singleton
    fun provideProfilesRepository(
        profilesDao: com.mili.eclipsereads.data.local.dao.ProfilesDao,
        profilesDataSource: SupabaseProfilesDataSource,
        @ApplicationContext context: Context
    ): ProfilesRepository {
        return ProfilesRepository(profilesDao, profilesDataSource, context)
    }

    @Provides
    @Singleton
    fun provideFavoritesRepository(
        database: AppDatabase,
        favoritesDataSource: SupabaseFavoritesDataSource,
        connectivityObserver: NetworkConnectivityObserver
    ): FavoritesRepository {
        return FavoritesRepository(database, favoritesDataSource, connectivityObserver)
    }
}
