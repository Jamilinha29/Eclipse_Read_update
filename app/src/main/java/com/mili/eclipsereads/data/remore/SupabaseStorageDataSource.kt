package com.mili.eclipsereads.data.remore

import io.github.jan.supabase.storage.Storage
import javax.inject.Inject

class SupabaseStorageDataSource @Inject constructor(
    private val storage: Storage
) {
    // Implementar funções de upload e download de ficheiros aqui
}
