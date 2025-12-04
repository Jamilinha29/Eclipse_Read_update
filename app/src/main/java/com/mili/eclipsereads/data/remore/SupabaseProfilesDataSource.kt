package com.mili.eclipsereads.data.remore

import com.mili.eclipsereads.domain.models.Profile
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import java.util.UUID
import javax.inject.Inject

class SupabaseProfilesDataSource @Inject constructor(
    private val postgrest: Postgrest,
    private val storage: Storage
) {

    suspend fun getProfile(userId: String): Profile? {
        return postgrest.from("profiles").select { filter {
            eq("id", userId)
        } }.decodeSingleOrNull<Profile>()
    }

    suspend fun updateProfile(profile: Profile): Profile? {
        return postgrest.from("profiles").update(profile).decodeSingleOrNull()
    }

    suspend fun uploadAvatar(userId: String, imageBytes: ByteArray): String {
        val bucket = storage.from("avatars")
        val fileName = "$userId/${UUID.randomUUID()}"
        
        bucket.upload(fileName, imageBytes, upsert = true)

        val publicUrl = bucket.publicUrl(fileName)

        postgrest.from("profiles").update({ "avatar_url" to publicUrl }) { filter {
            eq("id", userId)
        } }

        return publicUrl
    }
}