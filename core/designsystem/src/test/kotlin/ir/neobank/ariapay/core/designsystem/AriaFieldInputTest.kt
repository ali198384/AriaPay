package ir.neobank.ariapay.core.designsystem


import com.google.common.truth.Truth.assertThat
import ir.neobank.ariapay.core.designsystem.component.AriaFieldType
import ir.neobank.ariapay.core.designsystem.component.groupThousands
import ir.neobank.ariapay.core.designsystem.component.sanitizeFieldInput
import ir.neobank.ariapay.core.designsystem.component.thousandSeparatorsBefore
import org.junit.jupiter.api.Test


class AriaFieldInputTest {

    @Test
    fun `مبلغ رقم فارسی را لاتین می‌کند و صفر اضافه را برمی‌دارد`() {
        assertThat(sanitizeFieldInput(AriaFieldType.Amount, "۰۰۱۲a۳")).isEqualTo("123")
    }

    @Test
    fun `مبلغ صفر تنها صفر می‌ماند`() {
        assertThat(sanitizeFieldInput(AriaFieldType.Amount, "۰۰۰")).isEqualTo("0")
    }

    @Test
    fun `موبایل بیشتر از ۱۱ رقم نمی‌پذیرد`() {
        assertThat(sanitizeFieldInput(AriaFieldType.Phone, "0912-345-67890"))
            .isEqualTo("09123456789")
    }

    @Test
    fun `شبا حروف را حذف می‌کند`() {
        assertThat(sanitizeFieldInput(AriaFieldType.Sheba, "IR12-3456"))
            .isEqualTo("123456")
    }

    @Test
    fun `جداکننده از سمت راست وارد می‌شود`() {
        assertThat(groupThousands("12500000")).isEqualTo("12٬500٬000")
    }

    @Test
    fun `مکان‌نما جداکننده را در شمارش خودش نمی‌آورد`() {
        assertThat(thousandSeparatorsBefore(length = 7, offset = 1)).isEqualTo(0)
        assertThat(thousandSeparatorsBefore(length = 7, offset = 4)).isEqualTo(1)
        assertThat(thousandSeparatorsBefore(length = 7, offset = 7)).isEqualTo(2)
    }
}
