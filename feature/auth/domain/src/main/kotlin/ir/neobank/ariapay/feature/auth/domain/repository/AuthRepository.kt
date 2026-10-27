package ir.neobank.ariapay.feature.auth.domain.repository

import ir.neobank.ariapay.feature.auth.domain.AuthResult
import ir.neobank.ariapay.feature.auth.domain.AuthSession
import ir.neobank.ariapay.feature.auth.domain.MobileNumber
import ir.neobank.ariapay.feature.auth.domain.OtpCode


interface AuthRepository {
    suspend fun requestOtp(mobile: MobileNumber): AuthResult<Unit>
    suspend fun verifyOtp(mobile: MobileNumber, code: OtpCode): AuthResult<AuthSession>
}
