package ir.neobank.ariapay.feature.profile.domain

import kotlinx.coroutines.flow.Flow

class GetProfileUseCase(
    private val repository: ProfileRepository,
) {
    operator fun invoke(userId: String): Flow<UserProfile?> {
        val normalizedUserId = userId.trim()
        require(normalizedUserId.isNotEmpty()) { "User ID must not be blank" }
        return repository.observeProfile(normalizedUserId)
    }
}
