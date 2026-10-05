package ir.neobank.ariapay.core.domain.auth


import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AuthModelsTest {

    @Test
    fun `شماره فارسی معتبر نرمال و پذیرفته می‌شود`() {
        val mobile = MobileNumber.parse("۰۹۱۲۳۴۵۶۷۸۹")

        assertThat(mobile?.value).isEqualTo("09123456789")
        assertThat(MobileNumber.parse("٠٩١٢٣٤٥٦٧٨٩")?.value).isEqualTo("09123456789")
    }

    @Test
    fun `شماره کوتاه یا بدون صفر پذیرفته نمی‌شود`() {
        assertThat(MobileNumber.parse("9123456789")).isNull()
        assertThat(MobileNumber.parse("09123")).isNull()
        assertThat(MobileNumber.parse("abc09123456789xyz")).isNull()
    }

    @Test
    fun `نمایش شماره موبایل داده کامل را افشا نمی‌کند`() {
        val mobile = MobileNumber.parse("09123456789")

        assertThat(mobile.toString()).doesNotContain("09123456789")
    }

    @Test
    fun `کد شش‌رقمی فارسی پذیرفته می‌شود`() {
        assertThat(OtpCode.parse("۱۲۳۴۵۶")?.value).isEqualTo("123456")
    }

    @Test
    fun `رقم‌های عربی کد OTP به لاتین نرمال می‌شوند`() {
        assertThat(OtpCode.parse("١٢٣٤٥٦")?.value).isEqualTo("123456")
    }

    @Test
    fun `کد پنج‌رقمی رد می‌شود`() {
        assertThat(OtpCode.parse("12345")).isNull()
        assertThat(OtpCode.parse("abc123456")).isNull()
    }

    @Test
    fun `نمایش کد یک‌بارمصرف مقدار آن را افشا نمی‌کند`() {
        val otp = OtpCode.parse("123456")

        assertThat(otp.toString()).doesNotContain("123456")
    }

    @Test
    fun `توکن خالی نشست نمی‌سازد`() {
        assertThrows<IllegalArgumentException> {
            AuthSession(" ")
        }
    }

    @Test
    fun `نمایش نشست توکن دسترسی را افشا نمی‌کند`() {
        val session = AuthSession("demo-access-token")

        assertThat(session.toString()).doesNotContain("demo-access-token")
    }
}
