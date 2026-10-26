package ir.neobank.ariapay.feature.profile.domain

class LogoutUseCase(
    private val repository: ProfileRepository,
) {
    suspend operator fun invoke() = repository.logout()
}
