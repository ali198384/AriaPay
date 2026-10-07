package ir.neobank.ariapay.core.network.auth


import ir.neobank.ariapay.core.common.util.FinancialInputNormalizer
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration.Companion.milliseconds

class FakeAuthRemoteDataSourceImpl(
    private val nowMillis: () -> Long = System::currentTimeMillis,
) : AuthRemoteDataSource {

    private val pendingUntilMillis = mutableMapOf<String, Long>()
    private val pendingOtpMutex = Mutex()

    override suspend fun requestOtp(mobile: String): AuthRemoteResponse {
        delay(REQUEST_DELAY_MILLIS.milliseconds)
        val normalized = FinancialInputNormalizer.normalizeMobileNumber(mobile)
            ?: return AuthRemoteResponse(error = AuthRemoteError.InvalidMobile)
        pendingOtpMutex.withLock {
            val now = nowMillis()
            pendingUntilMillis.entries.removeAll { it.value <= now }
            pendingUntilMillis[normalized] = now + OTP_TTL_MILLIS
        }
        return AuthRemoteResponse()
    }

    override suspend fun verifyOtp(mobile: String, code: String): AuthRemoteResponse {
        delay(REQUEST_DELAY_MILLIS.milliseconds)
        val normalizedMobile = FinancialInputNormalizer.normalizeMobileNumber(mobile)
            ?: return AuthRemoteResponse(error = AuthRemoteError.InvalidMobile)
        val normalizedCode = FinancialInputNormalizer.normalizeOtpCode(code)
            ?: return AuthRemoteResponse(error = AuthRemoteError.WrongCode)
        return pendingOtpMutex.withLock {
            val expiresAt = pendingUntilMillis[normalizedMobile]
            when {
                expiresAt == null -> AuthRemoteResponse(error = AuthRemoteError.Expired)
                nowMillis() >= expiresAt -> {
                    pendingUntilMillis.remove(normalizedMobile)
                    AuthRemoteResponse(error = AuthRemoteError.Expired)
                }
                normalizedCode != DEMO_OTP -> AuthRemoteResponse(error = AuthRemoteError.WrongCode)
                else -> {
                    pendingUntilMillis.remove(normalizedMobile)
                    AuthRemoteResponse(
                        session = AuthRemoteSession(accessToken = "fake-access-token")
                    )
                }
            }
        }
    }

    private companion object {
        const val DEMO_OTP = "123456"
        const val OTP_TTL_MILLIS = 120_000L
        const val REQUEST_DELAY_MILLIS = 300L
    }
}
