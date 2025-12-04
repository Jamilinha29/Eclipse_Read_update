package com.mili.eclipsereads.data.remore

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.gotrue.user.User
import io.github.jan.supabase.gotrue.user.UserSession
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Inject

class SupabaseAuthDataSource @Inject constructor(
    private val auth: Auth,
    private val postgrest: Postgrest
) {

    fun getCurrentUser(): User? {
        return auth.currentUserOrNull
    }

    fun getCurrentUserId(): String? {
        return getCurrentUser()?.id
    }

    // GOOGLE ----------------------------------------------------

    suspend fun signInWithGoogle(idToken: String): UserSession? {
        auth.signInWith(Google) {
            this.idToken = idToken
        }
        return auth.currentSessionOrNull
    }

    // EMAIL / SENHA ---------------------------------------------

    suspend fun signInWithEmail(email: String, password: String): UserSession? {
        auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
        return auth.currentSessionOrNull
    }

    suspend fun signUpWithEmail(email: String, password: String): UserSession? {
        auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
        return auth.currentSessionOrNull
    }

    suspend fun signOut() {
        auth.signOut()
    }
}
