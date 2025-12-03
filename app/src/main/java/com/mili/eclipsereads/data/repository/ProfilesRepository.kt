package com.mili.eclipsereads.data.repository

import com.mili.eclipsereads.data.local.dao.ProfilesDao
import com.mili.eclipsereads.data.local.entities.toDomainModel
import com.mili.eclipsereads.data.local.entities.toEntity
import com.mili.eclipsereads.data.remore.SupabaseProfilesDataSource
import com.mili.eclipsereads.domain.models.Profile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProfilesRepository @Inject constructor(
    private val profilesDao: ProfilesDao,
    private val profilesDataSource: SupabaseProfilesDataSource
) {

    fun getProfile(userId: String): Flow<Profile?> {
        return profilesDao.getProfile(userId).map { it?.toDomainModel() }
    }

    suspend fun refreshProfile(userId: String) {
        val profile = profilesDataSource.getProfile(userId)
        profile?.let { profilesDao.insert(it.toEntity()) }
    }

    suspend fun updateProfile(profile: Profile) {
        val updatedProfile = profilesDataSource.updateProfile(profile)
        updatedProfile?.let { profilesDao.insert(it.toEntity()) }
    }
}
