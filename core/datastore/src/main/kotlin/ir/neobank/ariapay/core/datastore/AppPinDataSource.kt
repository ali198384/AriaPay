package ir.neobank.ariapay.core.datastore

import kotlinx.coroutines.flow.Flow

interface AppPinDataSource {
    val hasPin: Flow<Boolean>

    suspend fun savePin(pin: String)

    suspend fun verifyPin(candidate: String): Boolean

    suspend fun clearPin()
}
