package ir.neobank.ariapay.core.domain.profile

class LogoutUseCase(
    private val repository: ProfileRepository,
) {
    suspend operator fun invoke() = repository.logout()
}
