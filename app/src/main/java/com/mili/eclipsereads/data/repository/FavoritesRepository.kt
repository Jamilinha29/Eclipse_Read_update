package com.mili.eclipsereads.data.repository

import com.mili.eclipsereads.data.local.dao.FavoritesDao
import com.mili.eclipsereads.data.local.entities.toDomainModel
import com.mili.eclipsereads.data.local.entities.toEntity
import com.mili.eclipsereads.data.remore.SupabaseFavoritesDataSource
import com.mili.eclipsereads.domain.models.Favorite
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoritesRepository @Inject constructor(
    private val favoritesDao: FavoritesDao,
    private val favoritesDataSource: SupabaseFavoritesDataSource
) {

    fun getFavoritesForUser(userId: String): Flow<List<Favorite>> {
        return favoritesDao.getFavoritesForUser(userId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    suspend fun addFavorite(favorite: Favorite) {
        favoritesDataSource.addFavorite(favorite)
        favoritesDao.insert(favorite.toEntity())
    }

    suspend fun removeFavorite(userId: String, bookId: Int) {
        favoritesDataSource.removeFavorite(userId, bookId)
        favoritesDao.deleteFavorite(userId, bookId)
    }

    suspend fun refreshFavorites(userId: String) {
        val favorites = favoritesDataSource.getFavoritesForUser(userId)
        favoritesDao.deleteAllForUser(userId)
        favorites.forEach { favoritesDao.insert(it.toEntity()) }
    }
}