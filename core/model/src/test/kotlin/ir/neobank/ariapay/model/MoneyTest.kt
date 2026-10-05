package ir.neobank.ariapay.model


import com.google.common.truth.Truth.assertThat
import ir.neobank.ariapay.core.model.Money
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MoneyTest {

    @Test
    fun `rial is the storage unit`() {
        val money = Money.rials(10_000L)
        assertThat(money.amountInRials).isEqualTo(10_000L)
    }

    @Test
    fun `convert rial to toman correctly`() {
        val money = Money.rials(100_000L)
        assertThat(money.toTomans()).isEqualTo(10_000L)
    }

    @Test
    fun `toman converts to rial by multiplying 10`() {
        val money = Money.tomans(1_000L) // ۱۰۰۰ تومان = ۱۰٬۰۰۰ ریال
        assertThat(money.amountInRials).isEqualTo(10_000L)
        assertThat(money.toTomans()).isEqualTo(1_000L)
    }

    @Test
    fun `addition keeps rial precision`() {
        val sum = Money.rials(100) + Money.rials(250)
        assertThat(sum.amountInRials).isEqualTo(350L)
    }

    @Test
    fun `addition rejects overflow instead of wrapping around`() {
        assertThrows<ArithmeticException> {
            Money.rials(Long.MAX_VALUE) + Money.rials(1L)
        }
    }

    @Test
    fun `toman conversion rejects overflow`() {
        assertThrows<ArithmeticException> {
            Money.tomans(Long.MAX_VALUE / 10L + 1L)
        }
    }

    @Test
    fun `negative money is rejected`() {
        assertThrows<IllegalArgumentException> {
            Money.rials(-1)
        }
    }

    @Test
    fun `toman conversion floors extra rial`() {
        // ۳ ریال = ۰ تومان (تقسیم صحیح)
        assertThat(Money.rials(3).toTomans()).isEqualTo(0L)
        assertThat(Money.rials(10).toTomans()).isEqualTo(1L)
    }
}
