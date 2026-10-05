package ir.neobank.ariapay.core.common.validation

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class LuhnValidatorTest {

    @ParameterizedTest
    @ValueSource(
        strings = [
            "6037990000000006",      // ملی (ساخته‌شده با Luhn)
            "6274120000000002",      // صادرات
            "6219860000000001",      // سامان
            "4111111111111111",      // ویزای تستی معروف
            "6037-9900-0000-0006",   // با خط تیره
            "6037 9900 0000 0006",   // با فاصله
            "۶۰۳۷۹۹۰۰۰۰۰۰۰۰۰۶",      // ارقام فارسی
            "٦٠٣٧٩٩٠٠٠٠٠٠٠٠٠٦",      // ارقام عربی
        ]
    )
    fun `valid cards pass`(pan: String) {
        assertThat(LuhnValidator.isValid(pan)).isTrue()
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "6037990000000007",  // check digit غلط
            "1234567890123456",
            "603799000000000",   // ۱۵ رقم
            "60379900000000061", // ۱۷ رقم
            "",
            "60379900ABCD0006",
        ]
    )
    fun `invalid cards fail`(pan: String) {
        assertThat(LuhnValidator.isValid(pan)).isFalse()
    }
}
