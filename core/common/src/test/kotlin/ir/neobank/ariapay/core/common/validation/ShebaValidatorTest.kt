package ir.neobank.ariapay.core.common.validation


import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class ShebaValidatorTest {

    @ParameterizedTest
    @ValueSource(
        strings = [
            "IR510550011775005110110001",
            "ir510550011775005110110001",
            "IR51 0550 0117 7500 5110 1100 01",
        ]
    )
    fun `valid iranian sheba passes`(sheba: String) {
        assertThat(IranShebaValidator.isValid(sheba)).isTrue()
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "IR050171800000000001234568",  // checksum غلط
            "IR05017180000000000123456",   // کوتاه
            "050171800000000001234567",    // بدون IR
            "IR0501718000000000012345670", // بلند
            "US820170000000100324200001"   //غیر ایران
        ]
    )
    fun `invalid sheba fails`(sheba: String) {
        assertThat(IranShebaValidator.isValid(sheba)).isFalse()
    }
}
