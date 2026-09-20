package ir.neobank.ariapay.core.common.util


import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MoneyFormatterTest {

    @Test
    fun `groups thousands with persian separator and digits`() {
        assertThat(MoneyFormatter.formatRials(1_234_567L)).isEqualTo("۱٬۲۳۴٬۵۶۷ ریال")
    }

    @Test
    fun `toman is rial divided by 10`() {
        assertThat(MoneyFormatter.formatTomans(1_234_567L)).isEqualTo("۱۲۳٬۴۵۶ تومان")
    }

    @Test
    fun `latin digits when persianDigits is false`() {
        assertThat(MoneyFormatter.formatRials(1_234_567L, persianDigits = false)).isEqualTo("1٬234٬567 ریال")
    }

    @Test
    fun `zero rial`() {
        assertThat(MoneyFormatter.formatRials(0L)).isEqualTo("۰ ریال")
    }
}
