package ir.neobank.ariapay.core.domain.auth

@JvmInline
value class MobileNumber private constructor(val value: String) {
    val masked: String get() = "•".repeat(7) + value.takeLast(4)

    companion object {
        fun parse(raw: String): MobileNumber? {
            val normalized = raw.toEnglishDigits()
            if (normalized.any { it !in '0'..'9' && it != ' ' && it != '-' }) return null

            val digits = normalized.filter { it in '0'..'9' }
            if (digits.length != 11 || !digits.startsWith("09")) return null
            return MobileNumber(digits)
        }
    }

    override fun toString(): String = "MobileNumber($masked)"
}
