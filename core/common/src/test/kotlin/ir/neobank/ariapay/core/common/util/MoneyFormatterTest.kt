package ir.neobank.ariapay.core.common.util

import com.google.common.truth.Truth
import org.junit.jupiter.api.Test

class MoneyFormatterTest {

    @Test
    fun groupsThousandsWithPersianSeparatorAndDigits() {
        Truth.assertThat(MoneyFormatter.formatRials(1_234_567L)).isEqualTo("۱٬۲۳۴٬۵۶۷ ریال")
    }

    @Test
    fun `toman is rial divided by 10`() {
        Truth.assertThat(MoneyFormatter.formatTomans(1_234_567L)).isEqualTo("۱۲۳٬۴۵۶ تومان")
    }

    @Test
    fun `latin digits when persianDigits is false`() {
        Truth.assertThat(MoneyFormatter.formatRials(1_234_567L, persianDigits = false)).isEqualTo("1٬234٬567 ریال")
    }

    @Test
    fun `zero rial`() {
        Truth.assertThat(MoneyFormatter.formatRials(0L)).isEqualTo("۰ ریال")
    }

    @Test
    fun `formats the smallest Long without overflowing its absolute value`() {
        Truth.assertThat(MoneyFormatter.formatRials(Long.MIN_VALUE))
            .isEqualTo("−۹٬۲۲۳٬۳۷۲٬۰۳۶٬۸۵۴٬۷۷۵٬۸۰۸ ریال")
    }
}
