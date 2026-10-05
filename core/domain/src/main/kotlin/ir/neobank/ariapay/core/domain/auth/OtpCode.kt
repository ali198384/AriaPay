package ir.neobank.ariapay.core.domain.auth


@JvmInline
value class OtpCode private constructor(val value: String) {
    companion object {
        fun parse(raw: String): OtpCode? {
            val digits = raw.toEnglishDigits()
            if (digits.length != 6 || digits.any { it !in '0'..'9' }) return null
            return OtpCode(digits)
        }
    }

    override fun toString(): String = "OtpCode([REDACTED])"
}
