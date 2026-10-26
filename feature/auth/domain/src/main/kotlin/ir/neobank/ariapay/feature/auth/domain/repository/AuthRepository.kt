package ir.neobank.ariapay.feature.auth.domain


interface AuthRepository {
    suspend fun requestOtp(mobile: MobileNumber): AuthResult<Unit>
    suspend fun verifyOtp(mobile: MobileNumber, code: OtpCode): AuthResult<AuthSession>
}
