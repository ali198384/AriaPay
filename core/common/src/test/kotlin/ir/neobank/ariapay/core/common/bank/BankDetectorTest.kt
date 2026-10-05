package ir.neobank.ariapay.core.common.bank

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class BankDetectorTest {
    @Test
    fun `ayandeh bin maps to melli`() {
        val bank = BankDetector.findByCard("6362140000000003")
        assertThat(bank?.persianName).isEqualTo("بانک ملی")
        assertThat(bank?.legacyPersianName).isEqualTo("آینده")
    }

    @Test
    fun `ansar sheba maps to sepah`() {
        val bank = BankDetector.findBySheba("IR110630000000000000000001")
        assertThat(bank?.persianName).isEqualTo("بانک سپه")
        assertThat(bank?.legacyPersianName).isEqualTo("انصار")
    }

    @Test
    fun `refah bin is not maskan`() {
        val bank = BankDetector.findByCard("5894630000000002")
        assertThat(bank?.persianName).isEqualTo("بانک رفاه کارگران")
    }

    @Test
    fun `persian digits are normalized before bank detection`() {
        val cardBank = BankDetector.findByCard("۶۰۳۷۹۹۰۰۰۰۰۰۰۰۰۶")
        val shebaBank = BankDetector.findBySheba("IR۱۱۰۶۳۰۰۰۰۰۰۰۰۰۰۰۰۰۰۰۰۰۰۱")

        assertThat(cardBank?.persianName).isEqualTo("بانک ملی")
        assertThat(shebaBank?.persianName).isEqualTo("بانک سپه")
    }
}
