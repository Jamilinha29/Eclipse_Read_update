package com.mili.eclipsereads.data.remore

import com.mili.eclipsereads.domain.models.Profile
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import javax.inject.Inject

class SupabaseProfilesDataSource @Inject constructor(
    private val postgrest: Postgrest
) {
    suspend fun getProfile(userId: String): Profile? {
        return postgrest.from("profiles").select { filter {
            eq("profile_id", userId)
        } }.decodeSingleOrNull<Profile>()
    }

    suspend fun updateProfile(profile: Profile): Profile? {
        return postgrest.from("profiles").update(profile) { filter {
            eq("profile_id", profile.profileId)
        } }.decodeSingleOrNull<Profile>()
    }
}
