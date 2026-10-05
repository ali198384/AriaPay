package ir.neobank.ariapay.core.domain.auth


class RequestOtpUseCase(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(rawMobile: String): AuthResult<Unit> {
        val mobile = MobileNumber.parse(rawMobile)
            ?: return AuthResult.Failure(AuthError.InvalidMobile)
        return repository.requestOtp(mobile)
    }
}
