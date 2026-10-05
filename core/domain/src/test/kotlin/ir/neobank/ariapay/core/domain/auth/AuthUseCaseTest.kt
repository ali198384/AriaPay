package ir.neobank.ariapay.core.domain.auth


import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class AuthUseCaseTest {

    @Test
    fun `شماره نامعتبر درخواست را به Repository نمی‌رساند`() = runTest {
        val repository = RecordingRepository()
        val useCase = RequestOtpUseCase(repository)

        val result = useCase("09123")

        assertThat(result).isEqualTo(AuthResult.Failure(AuthError.InvalidMobile))
        assertThat(repository.requestCalls).isEqualTo(0)
    }

    @Test
    fun `شماره معتبر همان نتیجه Repository را برمی‌گرداند`() = runTest {
        val repository = RecordingRepository(
            requestResult = AuthResult.Failure(AuthError.Network),
        )
        val useCase = RequestOtpUseCase(repository)

        val result = useCase("09123456789")

        assertThat(result).isEqualTo(AuthResult.Failure(AuthError.Network))
        assertThat(repository.requestCalls).isEqualTo(1)
    }

    @Test
    fun `تأیید با شماره نامعتبر Repository را صدا نمی‌زند`() = runTest {
        val repository = RecordingRepository()
        val useCase = VerifyOtpUseCase(repository)

        val result = useCase("09123", "123456")

        assertThat(result).isEqualTo(AuthResult.Failure(AuthError.InvalidMobile))
        assertThat(repository.verifyCalls).isEqualTo(0)
    }

    @Test
    fun `کد با شکل نادرست InvalidOtp است و Repository صدا زده نمی‌شود`() = runTest {
        val repository = RecordingRepository()
        val useCase = VerifyOtpUseCase(repository)

        val result = useCase("09123456789", "12345")

        assertThat(result).isEqualTo(AuthResult.Failure(AuthError.InvalidOtp))
        assertThat(repository.verifyCalls).isEqualTo(0)
    }

    @Test
    fun `شماره و کد معتبر به Repository می‌رسند`() = runTest {
        val session = AuthSession("GAPGPTMASKTOKENedx72m5dizbX0X")
        val repository = RecordingRepository(
            verifyResult = AuthResult.Success(session),
        )
        val useCase = VerifyOtpUseCase(repository)

        val result = useCase("09123456789", "123456")

        assertThat(result).isEqualTo(AuthResult.Success(session))
        assertThat(repository.verifyCalls).isEqualTo(1)
        assertThat(repository.lastMobile?.value).isEqualTo("09123456789")
        assertThat(repository.lastCode?.value).isEqualTo("123456")
    }

    private class RecordingRepository(
        private val requestResult: AuthResult<Unit> = AuthResult.Success(Unit),
        private val verifyResult: AuthResult<AuthSession> =
            AuthResult.Success(AuthSession("GAPGPTMASKTOKENedx72m5dizbX1X")),
    ) : AuthRepository {
        var requestCalls: Int = 0
        var verifyCalls: Int = 0
        var lastMobile: MobileNumber? = null
        var lastCode: OtpCode? = null

        override suspend fun requestOtp(mobile: MobileNumber): AuthResult<Unit> {
            requestCalls += 1
            lastMobile = mobile
            return requestResult
        }

        override suspend fun verifyOtp(
            mobile: MobileNumber,
            code: OtpCode,
        ): AuthResult<AuthSession> {
            verifyCalls += 1
            lastMobile = mobile
            lastCode = code
            return verifyResult
        }
    }
}
