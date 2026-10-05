package ir.neobank.ariapay.core.data.auth


import com.google.common.truth.Truth.assertThat
import ir.neobank.ariapay.core.datastore.AriaPreferencesDataSource
import ir.neobank.ariapay.core.domain.auth.AuthError
import ir.neobank.ariapay.core.domain.auth.AuthResult
import ir.neobank.ariapay.core.domain.auth.MobileNumber
import ir.neobank.ariapay.core.domain.auth.OtpCode
import ir.neobank.ariapay.core.network.auth.AuthRemoteDataSource
import ir.neobank.ariapay.core.network.auth.AuthRemoteError
import ir.neobank.ariapay.core.network.auth.AuthRemoteResponse
import ir.neobank.ariapay.core.network.auth.AuthRemoteSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.io.IOException

class FakeAuthRepositoryTest {

    @Test
    fun `تأیید موفق توکن را ذخیره و ورود را روشن می‌کند`() = runTest {
        val remote = StubRemote(
            AuthRemoteResponse(AuthRemoteSession("demo-access")),
        )
        val preferences = MemoryPreferences()
        val repository = FakeAuthRepository(remote, preferences)
        val mobile = MobileNumber.parse("09123456789")!!

        val requested = repository.requestOtp(mobile)
        val verified = repository.verifyOtp(mobile, OtpCode.parse("123456")!!)

        assertThat(requested).isEqualTo(AuthResult.Success(Unit))
        assertThat(verified).isInstanceOf(AuthResult.Success::class.java)
        assertThat(preferences.accessToken.first()).isEqualTo("demo-access")
        assertThat(preferences.isLoggedIn.first()).isTrue()
    }

    @Test
    fun `کد اشتباه توکن ذخیره نمی‌کند`() = runTest {
        val (repository, preferences) = failed(AuthRemoteError.WrongCode)
        val result = repository.verifyOtp(
            MobileNumber.parse("09123456789")!!,
            OtpCode.parse("000000")!!,
        )

        assertThat(result).isEqualTo(AuthResult.Failure(AuthError.WrongCode))
        assertThat(preferences.accessToken.first()).isNull()
        assertThat(preferences.isLoggedIn.first()).isFalse()
    }

    @Test
    fun `کد منقضی توکن ذخیره نمی‌کند`() = runTest {
        val (repository, preferences) = failed(AuthRemoteError.Expired)
        val result = repository.verifyOtp(
            MobileNumber.parse("09123456789")!!,
            OtpCode.parse("123456")!!,
        )

        assertThat(result).isEqualTo(AuthResult.Failure(AuthError.Expired))
        assertThat(preferences.accessToken.first()).isNull()
    }

    @Test
    fun `پاسخ موفق بدون نشست خطای شبکه می‌دهد`() = runTest {
        val preferences = MemoryPreferences()
        val repository = FakeAuthRepository(
            remote = StubRemote(AuthRemoteResponse()),
            preferences = preferences,
        )

        val result = repository.verifyOtp(
            MobileNumber.parse("09123456789")!!,
            OtpCode.parse("123456")!!,
        )

        assertThat(result).isEqualTo(AuthResult.Failure(AuthError.Network))
        assertThat(preferences.accessToken.first()).isNull()
        assertThat(preferences.isLoggedIn.first()).isFalse()
    }

    @Test
    fun `خطای ارتباط شبکه به نتیجه تبدیل می‌شود و توکن ذخیره نمی‌شود`() = runTest {
        val preferences = MemoryPreferences()
        val repository = FakeAuthRepository(
            remote = ThrowingRemote(IOException("network unavailable")),
            preferences = preferences,
        )
        val mobile = MobileNumber.parse("09123456789")!!
        val code = OtpCode.parse("123456")!!

        val requestResult = repository.requestOtp(mobile)
        val verifyResult = repository.verifyOtp(mobile, code)

        assertThat(requestResult).isEqualTo(AuthResult.Failure(AuthError.Network))
        assertThat(verifyResult).isEqualTo(AuthResult.Failure(AuthError.Network))
        assertThat(preferences.accessToken.first()).isNull()
        assertThat(preferences.isLoggedIn.first()).isFalse()
    }

    private fun failed(error: AuthRemoteError): Pair<FakeAuthRepository, MemoryPreferences> {
        val preferences = MemoryPreferences()
        val repository = FakeAuthRepository(
            remote = StubRemote(
                AuthRemoteResponse(error = error),
            ),
            preferences = preferences,
        )
        return repository to preferences
    }

    private class StubRemote(
        private val response: AuthRemoteResponse,
    ) : AuthRemoteDataSource {
        override suspend fun requestOtp(mobile: String) = response
        override suspend fun verifyOtp(mobile: String, code: String) = response
    }

    private class ThrowingRemote(
        private val exception: IOException,
    ) : AuthRemoteDataSource {
        override suspend fun requestOtp(mobile: String): AuthRemoteResponse = throw exception
        override suspend fun verifyOtp(mobile: String, code: String): AuthRemoteResponse = throw exception
    }

    private class MemoryPreferences : AriaPreferencesDataSource {
        private val token = MutableStateFlow<String?>(null)
        private val loggedIn = MutableStateFlow(false)

        override val accessToken: Flow<String?> = token
        override val isLoggedIn: Flow<Boolean> = loggedIn

        override suspend fun setAccessToken(token: String?) {
            this@MemoryPreferences.token.value = token
        }

        override suspend fun setLoggedIn(value: Boolean) {
            loggedIn.value = value
        }

        override suspend fun saveSession(accessToken: String) {
            token.value = accessToken
            loggedIn.value = true
        }

        override suspend fun clearSession() { }
    }
}
