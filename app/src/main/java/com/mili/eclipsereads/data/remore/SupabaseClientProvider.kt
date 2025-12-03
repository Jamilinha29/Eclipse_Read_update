package com.mili.eclipsereads.data.remore

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseClientProvider {

    val supabase = createSupabaseClient(
        // TODO: Replace with your Supabase URL and Key
        supabaseUrl = "https://xyzcompany.supabase.co",
        supabaseKey = "publishable-or-anon-key"
    ) {
        install(Auth)
        install(Postgrest)
        install(Storage)
        //install other modules
    }
}