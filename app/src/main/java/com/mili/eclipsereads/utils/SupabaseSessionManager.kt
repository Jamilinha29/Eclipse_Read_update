package com.mili.eclipsereads.utils

import io.github.jan.supabase.gotrue.SessionManager
import io.github.jan.supabase.gotrue.user.UserSession
import javax.inject.Inject

class SupabaseSessionManager @Inject constructor(
    private val tokenManager: TokenManager
) : SessionManager {

    override fun loadSession(): UserSession? {
        return tokenManager.getSession()
    }

    override fun saveSession(session: UserSession) {
        tokenManager.saveSession(session)
    }

    override fun deleteSession() {
        tokenManager.clearSession()
    }
}