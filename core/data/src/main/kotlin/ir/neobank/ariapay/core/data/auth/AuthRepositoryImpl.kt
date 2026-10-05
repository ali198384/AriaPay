package ir.neobank.ariapay.core.data.auth

import ir.neobank.ariapay.core.datastore.AriaPreferencesDataSource
import ir.neobank.ariapay.core.domain.auth.AuthError
import ir.neobank.ariapay.core.domain.auth.AuthRepository
import ir.neobank.ariapay.core.domain.auth.AuthResult
import ir.neobank.ariapay.core.domain.auth.AuthSession
import ir.neobank.ariapay.core.domain.auth.MobileNumber
import ir.neobank.ariapay.core.domain.auth.OtpCode
import ir.neobank.ariapay.core.network.auth.AuthRemoteDataSource
import ir.neobank.ariapay.core.network.auth.AuthRemoteError
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl (
    private val remote: AuthRemoteDataSource,
    private val preferences: AriaPreferencesDataSource,
) : AuthRepository {

    override suspend fun requestOtp(mobile: MobileNumber): AuthResult<Unit> {
        val response = try {
            remote.requestOtp(mobile.value)
        } catch (_: IOException) {
            return AuthResult.Failure(AuthError.Network)
        }
        return if (response.isSuccess) {
            AuthResult.Success(Unit)
        } else {
            AuthResult.Failure(response.error.toDomain())
        }
    }

    override suspend fun verifyOtp(
        mobile: MobileNumber,
        code: OtpCode,
    ): AuthResult<AuthSession> {
        val response = try {
            remote.verifyOtp(mobile.value, code.value)
        } catch (_: IOException) {
            return AuthResult.Failure(AuthError.Network)
        }
        if (!response.isSuccess) {
            return AuthResult.Failure(response.error.toDomain())
        }

        val token = response.session?.accessToken
            ?: return AuthResult.Failure(AuthError.Network)

        preferences.apply {
            saveSession(token)
            setLoggedIn(true)
        }
        return AuthResult.Success(AuthSession(token))
    }
}

private fun AuthRemoteError?.toDomain(): AuthError = when (this) {
    AuthRemoteError.InvalidMobile -> AuthError.InvalidMobile
    AuthRemoteError.WrongCode -> AuthError.WrongCode
    AuthRemoteError.Expired -> AuthError.Expired
    null -> AuthError.Network
}
