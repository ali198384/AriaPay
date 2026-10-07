package ir.neobank.ariapay.core.common.validation

import ir.neobank.ariapay.core.common.util.FinancialInputNormalizer

object IranShebaValidator {

    fun isValid(raw: String): Boolean {
        val sheba = FinancialInputNormalizer.normalizeSheba(raw) ?: return false

        // ISO 13616: ۴ نویسه اول برود ته؛ A=10 … Z=35
        val rearranged = sheba.substring(4) + sheba.substring(0, 4)
        val numeric = buildString {
            for (ch in rearranged) {
                append(if (ch.isLetter()) (ch - 'A' + 10).toString() else ch)
            }
        }
        return numeric.mod97() == 1
    }

    // عدد ۲۸رقمی در Long جا نمی‌شود؛ باقی‌مانده را تکه‌تکه حساب می‌کنیم
    private fun String.mod97(): Int {
        var remainder = 0
        for (ch in this) {
            remainder = (remainder * 10 + (ch - '0')) % 97
        }
        return remainder
    }
}
