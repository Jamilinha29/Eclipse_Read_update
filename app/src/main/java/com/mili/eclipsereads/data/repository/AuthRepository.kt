package com.mili.eclipsereads.data.repository

import com.mili.eclipsereads.data.remore.SupabaseAuthDataSource
import io.github.jan.supabase.gotrue.user.User
import io.github.jan.supabase.gotrue.user.UserSession
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authDataSource: SupabaseAuthDataSource
) {

    fun getCurrentUser(): User? = 
        authDataSource.getCurrentUser()

    fun getCurrentUserId(): String? =
        authDataSource.getCurrentUserId()

    // GOOGLE
    suspend fun signInWithGoogle(idToken: String): UserSession? =
        authDataSource.signInWithGoogle(idToken)

    // EMAIL / SENHA
    suspend fun signInWithEmail(email: String, password: String): UserSession? =
        authDataSource.signInWithEmail(email, password)

    suspend fun signUpWithEmail(email: String, password: String): UserSession? =
        authDataSource.signUpWithEmail(email, password)

    suspend fun signOut() = authDataSource.signOut()
}
