package ir.neobank.ariapay.core.common.util

import com.google.common.truth.Truth
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CardMaskerTest {
    @Test
    fun `mask should keep first 6 and last 4 digits while masking middle 6 digits`() {
        val input = "6037991812345678"
        val expected = "6037-99**-****-5678"
        Truth.assertThat(CardMasker.mask(input)).isEqualTo(expected)
    }

    @Test
    fun `mask formatted card with hyphens should also mask correctly`() {
        val input = "6037-9918-1234-5678"
        val expected = "6037-99**-****-5678"
        Truth.assertThat(CardMasker.mask(input)).isEqualTo(expected)
    }

    @Test
    fun `when card length is invalid then return empty or safe fallback`() {
        val input = "123"
        assertThrows<IllegalArgumentException> {
            CardMasker.mask(input)
        }
    }
}