package ir.neobank.ariapay.core.common.util

import java.util.Locale
import kotlin.math.abs
import kotlin.text.iterator

object MoneyFormatter {

    fun formatRials(rials: Long, persianDigits: Boolean = true): String =
        formatAmount(rials, unit = "ریال", persianDigits)

    fun formatTomans(rials: Long, persianDigits: Boolean = true): String =
        formatAmount(rials / 10, unit = "تومان", persianDigits)

    private fun formatAmount(amount: Long, unit: String, persianDigits: Boolean): String {
        val sign = if (amount < 0) "-" else ""
        val grouped = "%,d".format(Locale.US, abs(amount)).replace(',', '٬')
        val body = if (persianDigits) toPersianDigits(sign + grouped) else sign + grouped
        return "$body $unit"
    }

    private fun toPersianDigits(input: String): String = buildString(input.length) {
        for (ch in input) {
            append(
                when (ch) {
                    in '0'..'9' -> '۰' + (ch - '0')
                    '-' -> '−' // minus یونیکد؛ خواناتر از hyphen در UI فارسی
                    else -> ch
                }
            )
        }
    }
}