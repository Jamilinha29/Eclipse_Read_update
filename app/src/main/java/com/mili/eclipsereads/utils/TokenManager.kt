package com.mili.eclipsereads.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.gotrue.user.UserSession
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(@ApplicationContext context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "encrypted_token_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveSession(session: UserSession) {
        val sessionJson = Json.encodeToString(session)
        sharedPreferences.edit().putString("user_session", sessionJson).apply()
    }

    fun getSession(): UserSession? {
        val sessionJson = sharedPreferences.getString("user_session", null)
        return sessionJson?.let { Json.decodeFromString<UserSession>(it) }
    }

    fun clearSession() {
        sharedPreferences.edit().remove("user_session").apply()
    }
}
