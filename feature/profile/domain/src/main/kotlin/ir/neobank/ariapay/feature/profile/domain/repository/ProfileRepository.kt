package ir.neobank.ariapay.feature.profile.domain

import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun observeProfile(userId: String): Flow<UserProfile?>

    suspend fun getProfile(userId: String): UserProfile?

    /** Saves a server profile snapshot locally so it remains available offline. */
    suspend fun saveProfile(profile: UserProfile)

    /** Returns false when no cached profile exists for [userId]. */
    suspend fun updateName(userId: String, fullName: String): Boolean

    /** Clears the authenticated session and cached profile data on this device. */
    suspend fun logout()
}
