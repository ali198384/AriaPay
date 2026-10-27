package ir.neobank.ariapay.feature.auth.domain.usecase

import ir.neobank.ariapay.feature.auth.domain.AuthError
import ir.neobank.ariapay.feature.auth.domain.AuthResult
import ir.neobank.ariapay.feature.auth.domain.AuthSession
import ir.neobank.ariapay.feature.auth.domain.MobileNumber
import ir.neobank.ariapay.feature.auth.domain.OtpCode
import ir.neobank.ariapay.feature.auth.domain.repository.AuthRepository


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
