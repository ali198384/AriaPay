package ir.neobank.ariapay.core.common.util

/** Shared canonicalization for Persian financial inputs. Validation algorithms stay in validators. */
object FinancialInputNormalizer {

    /** Converts Persian and Arabic-Indic decimal digits to ASCII without changing other characters. */
    fun toAsciiDigits(value: String): String = buildString(value.length) {
        for (char in value) {
            append(
                when (char.code) {
                    in PERSIAN_DIGITS -> ('0'.code + char.code - PERSIAN_DIGITS.first).toChar()
                    in ARABIC_INDIC_DIGITS -> ('0'.code + char.code - ARABIC_INDIC_DIGITS.first).toChar()
                    else -> char
                }
            )
        }
    }

    /** Keeps only ASCII digits after converting Persian and Arabic-Indic digits. */
    fun digitsOnly(value: String): String =
        toAsciiDigits(value).filter { it in ASCII_DIGITS }

    /** Returns an ASCII Iranian mobile number, or null when its shape or characters are invalid. */
    fun normalizeMobileNumber(value: String): String? {
        val ascii = toAsciiDigits(value)
        if (ascii.any { it !in ASCII_DIGITS && it != ' ' && it != '-' }) return null

        return ascii.filter { it in ASCII_DIGITS }
            .takeIf { it.length == MOBILE_NUMBER_LENGTH && it.startsWith(MOBILE_PREFIX) }
    }

    /** Returns a six-digit ASCII OTP, or null when it contains other characters or has wrong length. */
    fun normalizeOtpCode(value: String): String? = toAsciiDigits(value)
        .takeIf { it.length == OTP_LENGTH && it.all { char -> char in ASCII_DIGITS } }

    /** Removes display separators and returns ASCII card digits; callers enforce their own length. */
    fun normalizeCardNumber(value: String): String? {
        val ascii = toAsciiDigits(value)
        if (ascii.any { it !in ASCII_DIGITS && !it.isWhitespace() && it != '-' }) return null
        return ascii.filter { it in ASCII_DIGITS }
    }

    /** Removes whitespace, uppercases the country code, and checks the Iranian IBAN shape. */
    fun normalizeSheba(value: String): String? {
        val normalized = toAsciiDigits(value.filterNot(Char::isWhitespace)).uppercase()
        if (normalized.length != SHEBA_LENGTH || !normalized.startsWith(SHEBA_PREFIX)) return null
        if (!normalized.drop(SHEBA_PREFIX.length).all { it in ASCII_DIGITS }) return null
        return normalized
    }

    private val ASCII_DIGITS = '0'..'9'
    private val PERSIAN_DIGITS = 0x06F0..0x06F9
    private val ARABIC_INDIC_DIGITS = 0x0660..0x0669

    private const val MOBILE_NUMBER_LENGTH = 11
    private const val MOBILE_PREFIX = "09"
    private const val OTP_LENGTH = 6
    private const val SHEBA_LENGTH = 26
    private const val SHEBA_PREFIX = "IR"
}
