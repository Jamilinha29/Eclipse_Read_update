package com.mili.eclipsereads.data.repository

import com.mili.eclipsereads.data.local.db.AppDatabase
import com.mili.eclipsereads.data.local.entities.DroppedBookEntity
import com.mili.eclipsereads.data.local.entities.toDomainModel
import com.mili.eclipsereads.data.local.entities.toEntity
import com.mili.eclipsereads.data.remore.SupabaseFavoritesDataSource
import com.mili.eclipsereads.domain.models.Favorite
import com.mili.eclipsereads.utils.NetworkConnectivityObserver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class FavoritesRepository @Inject constructor(
    private val database: AppDatabase,
    private val favoritesDataSource: SupabaseFavoritesDataSource,
    private val connectivityObserver: NetworkConnectivityObserver
) {

    private val favoritesDao = database.favoritesDao()
    private val droppedBookDao = database.droppedBookDao()

    fun getFavoritesForUser(userId: String): Flow<List<Favorite>> {
        return favoritesDao.getFavoritesForUser(userId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    suspend fun addFavorite(favorite: Favorite) {
        favoritesDao.insert(favorite.toEntity())
        if (connectivityObserver.isOnline.first()) {
            try {
                favoritesDataSource.addFavorite(favorite)
            } catch (e: Exception) {
                Timber.e(e, "Failed to add favorite to remote source")
            }
        }
    }

    suspend fun removeFavorite(userId: String, bookId: Int) {
        // Log the dropped book before removing it from favorites
        droppedBookDao.insert(DroppedBookEntity(userId = userId, bookId = bookId))
        
        favoritesDao.deleteFavorite(userId, bookId)
        if (connectivityObserver.isOnline.first()) {
            try {
                favoritesDataSource.removeFavorite(userId, bookId)
            } catch (e: Exception) {
                Timber.e(e, "Failed to remove favorite from remote source")
            }
        }
    }

    suspend fun refreshFavorites(userId: String) {
        if (connectivityObserver.isOnline.first()) {
            val favorites = favoritesDataSource.getFavoritesForUser(userId)
            // Consider a more sophisticated sync mechanism in the future
            // to avoid overwriting local-only changes.
            favoritesDao.deleteAllForUser(userId)
            favorites.forEach { favoritesDao.insert(it.toEntity()) }
        }
    }
}