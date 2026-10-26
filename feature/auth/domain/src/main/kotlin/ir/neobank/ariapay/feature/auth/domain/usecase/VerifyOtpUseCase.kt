package ir.neobank.ariapay.feature.auth.domain


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
