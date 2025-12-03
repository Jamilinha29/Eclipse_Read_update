package com.mili.eclipsereads.data.repository

import com.mili.eclipsereads.data.remore.SupabaseAuthDataSource
import io.github.jan.supabase.gotrue.user.User
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authDataSource: SupabaseAuthDataSource
) {

    fun getCurrentUser(): User? = 
        authDataSource.getCurrentUser()

    fun getCurrentUserId(): String? =
        authDataSource.getCurrentUserId()

    // GOOGLE
    suspend fun signInWithGoogle(idToken: String): String? =
        authDataSource.signInWithGoogle(idToken)

    // EMAIL / SENHA
    suspend fun signInWithEmail(email: String, password: String): String? =
        authDataSource.signInWithEmail(email, password)

    suspend fun signUpWithEmail(email: String, password: String): String? =
        authDataSource.signUpWithEmail(email, password)

    fun signOut() = authDataSource.signOut()
}
