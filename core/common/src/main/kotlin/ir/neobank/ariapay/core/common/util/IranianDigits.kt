package ir.neobank.ariapay.core.common.util

/** Converts Persian and Arabic-Indic decimal digits to ASCII digits. */
object IranianDigits {
    fun toEnglish(value: String): String = buildString(value.length) {
        for (char in value) {
            append(
                when (char) {
                    in '۰'..'۹' -> '0' + (char - '۰')
                    in '٠'..'٩' -> '0' + (char - '٠')
                    else -> char
                }
            )
        }
    }
}
