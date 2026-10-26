package ir.neobank.ariapay.feature.auth.domain


class RequestOtpUseCase(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(rawMobile: String): AuthResult<Unit> {
        val mobile = MobileNumber.parse(rawMobile)
            ?: return AuthResult.Failure(AuthError.InvalidMobile)
        return repository.requestOtp(mobile)
    }
}
