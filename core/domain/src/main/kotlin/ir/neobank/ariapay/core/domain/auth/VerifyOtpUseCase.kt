package ir.neobank.ariapay.core.domain.auth


class VerifyOtpUseCase(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(
        rawMobile: String,
        rawCode: String,
    ): AuthResult<AuthSession> {
        val mobile = MobileNumber.parse(rawMobile)
            ?: return AuthResult.Failure(AuthError.InvalidMobile)
        val code = OtpCode.parse(rawCode)
            ?: return AuthResult.Failure(AuthError.InvalidOtp)
        return repository.verifyOtp(mobile, code)
    }
}
