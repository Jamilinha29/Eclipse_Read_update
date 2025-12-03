package com.mili.eclipsereads.data.remore

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.gotrue.user.User
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

    suspend fun signInWithGoogle(idToken: String): String? {
        auth.signInWith(Google) {
            this.idToken = idToken
        }
        return auth.currentSessionOrNull?.accessToken
    }

    // EMAIL / SENHA ---------------------------------------------

    suspend fun signInWithEmail(email: String, password: String): String? {
        auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
        // você também pode usar auth.currentAccessTokenOrNull()
        return auth.currentSessionOrNull?.accessToken
    }

    suspend fun signUpWithEmail(email: String, password: String): String? {
        auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
        return auth.currentSessionOrNull?.accessToken
    }

    fun signOut() {
        auth.signOut()
    }
}
