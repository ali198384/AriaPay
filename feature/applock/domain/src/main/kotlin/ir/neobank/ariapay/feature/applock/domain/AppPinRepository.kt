package ir.neobank.ariapay.feature.applock.domain

import kotlinx.coroutines.flow.Flow

interface AppPinRepository {
    val hasPin: Flow<Boolean>

    suspend fun savePin(pin: String)

    suspend fun verifyPin(candidate: String): Boolean

    suspend fun clearPin()
}
