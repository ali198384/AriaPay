package ir.neobank.ariapay.feature.profile.domain.usecase

import ir.neobank.ariapay.feature.profile.domain.repository.ProfileRepository

class LogoutUseCase(
    private val repository: ProfileRepository,
) {
    suspend operator fun invoke() = repository.logout()
}
