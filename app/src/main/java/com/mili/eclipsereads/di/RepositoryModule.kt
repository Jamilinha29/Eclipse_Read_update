package com.mili.eclipsereads.di

import com.mili.eclipsereads.data.local.dao.*
import com.mili.eclipsereads.data.remore.*
import com.mili.eclipsereads.data.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
    fun provideBooksRepository(booksDao: BooksDao, booksDataSource: SupabaseBooksDataSource): BooksRepository {
        return BooksRepository(booksDao, booksDataSource)
    }

    @Provides
    @Singleton
    fun provideReadingRepository(readingDao: ReadingDao, readingDataSource: SupabaseReadingDataSource): ReadingRepository {
        return ReadingRepository(readingDao, readingDataSource)
    }

    @Provides
    @Singleton
    fun provideReviewsRepository(reviewsDao: ReviewsDao, reviewsDataSource: SupabaseReviewsDataSource): ReviewsRepository {
        return ReviewsRepository(reviewsDao, reviewsDataSource)
    }

    @Provides
    @Singleton
    fun provideProfilesRepository(profilesDao: ProfilesDao, profilesDataSource: SupabaseProfilesDataSource): ProfilesRepository {
        return ProfilesRepository(profilesDao, profilesDataSource)
    }

    @Provides
    @Singleton
    fun provideFavoritesRepository(favoritesDao: FavoritesDao, favoritesDataSource: SupabaseFavoritesDataSource): FavoritesRepository {
        return FavoritesRepository(favoritesDao, favoritesDataSource)
    }
}
