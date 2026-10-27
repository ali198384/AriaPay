package ir.neobank.ariapay.feature.auth.domain.usecase

import ir.neobank.ariapay.feature.auth.domain.AuthError
import ir.neobank.ariapay.feature.auth.domain.AuthResult
import ir.neobank.ariapay.feature.auth.domain.MobileNumber
import ir.neobank.ariapay.feature.auth.domain.repository.AuthRepository


class RequestOtpUseCase(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(rawMobile: String): AuthResult<Unit> {
        val mobile = MobileNumber.parse(rawMobile)
            ?: return AuthResult.Failure(AuthError.InvalidMobile)
        return repository.requestOtp(mobile)
    }
}
