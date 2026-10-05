package ir.neobank.ariapay.core.domain.auth


interface AuthRepository {
    suspend fun requestOtp(mobile: MobileNumber): AuthResult<Unit>
    suspend fun verifyOtp(mobile: MobileNumber, code: OtpCode): AuthResult<AuthSession>
}
