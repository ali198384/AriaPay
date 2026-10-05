package ir.neobank.ariapay.core.network.auth


import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class FakeAuthRemoteDataSourceImplTest {

    private class Clock(var millis: Long = 1_000_000L) {
        fun advanceSeconds(seconds: Long) {
            millis += seconds * 1_000
        }
    }

    private fun source(clock: Clock) = FakeAuthRemoteDataSourceImpl(
        nowMillis = { clock.millis },
    )

    @Test
    fun `remote session string does not expose its access token`() {
        val session = AuthRemoteSession("remote-secret-token")

        assertThat(session.toString()).doesNotContain("remote-secret-token")
    }

    @Test
    fun `درخواست با شماره معتبر موفق است`() = runTest {
        val clock = Clock()
        val result = source(clock).requestOtp("09123456789")

        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `شماره نامعتبر رد می‌شود`() = runTest {
        val result = source(Clock()).requestOtp("12345")

        assertThat(result.error).isEqualTo(AuthRemoteError.InvalidMobile)
    }

    @Test
    fun `متن اضافی کنار شماره موبایل پذیرفته نمی‌شود`() = runTest {
        val result = source(Clock()).requestOtp("abc09123456789xyz")

        assertThat(result.error).isEqualTo(AuthRemoteError.InvalidMobile)
    }

    @Test
    fun `کد ۱۲۳۴۵۶ بلافاصله بعد از درخواست پذیرفته می‌شود`() = runTest {
        val clock = Clock()
        val source = source(clock)
        source.requestOtp("09123456789")

        val result = source.verifyOtp("09123456789", "123456")

        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `کد OTP پس از مصرف دوباره پذیرفته نمی‌شود`() = runTest {
        val source = source(Clock())
        source.requestOtp("09123456789")

        val firstResult = source.verifyOtp("09123456789", "123456")
        val secondResult = source.verifyOtp("09123456789", "123456")

        assertThat(firstResult.isSuccess).isTrue()
        assertThat(secondResult.error).isEqualTo(AuthRemoteError.Expired)
    }

    @Test
    fun `کد اشتباه رد می‌شود`() = runTest {
        val clock = Clock()
        val source = source(clock)
        source.requestOtp("09123456789")

        val result = source.verifyOtp("09123456789", "000000")

        assertThat(result.error).isEqualTo(AuthRemoteError.WrongCode)
    }

    @Test
    fun `متن اضافی کنار کد OTP پذیرفته نمی‌شود`() = runTest {
        val source = source(Clock())
        source.requestOtp("09123456789")

        val result = source.verifyOtp("09123456789", "abc123456")

        assertThat(result.error).isEqualTo(AuthRemoteError.WrongCode)
    }

    @Test
    fun `بعد از انقضا همان کد درست هم رد می‌شود`() = runTest {
        val clock = Clock()
        val source = source(clock)
        source.requestOtp("09123456789")
        clock.advanceSeconds(121)

        val result = source.verifyOtp("09123456789", "123456")

        assertThat(result.error).isEqualTo(AuthRemoteError.Expired)
    }

    @Test
    fun `بدون درخواست قبلی، کد پذیرفته نمی‌شود`() = runTest {
        val result = source(Clock()).verifyOtp("09123456789", "123456")

        assertThat(result.isSuccess).isFalse()
    }
}
