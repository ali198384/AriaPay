package ir.neobank.ariapay.feature.profile.domain.usecase

import ir.neobank.ariapay.feature.profile.domain.model.UserProfile
import ir.neobank.ariapay.feature.profile.domain.repository.ProfileRepository
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
