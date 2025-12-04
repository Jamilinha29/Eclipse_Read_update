package com.mili.eclipsereads.di

import android.content.Context
import com.mili.eclipsereads.utils.ConnectivityObserver
import com.mili.eclipsereads.utils.NetworkConnectivityObserver
import com.mili.eclipsereads.utils.SupabaseSessionManager
import com.mili.eclipsereads.utils.TokenManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.gotrue.SessionManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UtilsModule {

    @Binds
    @Singleton
    abstract fun bindConnectivityObserver(impl: NetworkConnectivityObserver): ConnectivityObserver

    @Binds
    @Singleton
    abstract fun bindSessionManager(impl: SupabaseSessionManager): SessionManager

    companion object {
        @Provides
        @Singleton
        fun provideTokenManager(@ApplicationContext context: Context): TokenManager {
            return TokenManager(context)
        }
    }
}
