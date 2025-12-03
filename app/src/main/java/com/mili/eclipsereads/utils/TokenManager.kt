package com.mili.eclipsereads.utils

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(@ApplicationContext context: Context) {

    private val sharedPreferences: SharedPreferences
    private val editor: SharedPreferences.Editor

    init {
        sharedPreferences = context.getSharedPreferences("token_prefs", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }

    fun saveToken(token: String) {
        editor.putString("auth_token", token).commit()
    }

    fun getToken(): String? {
        return sharedPreferences.getString("auth_token", null)
    }

    fun clearToken() {
        editor.remove("auth_token").commit()
    }
}
