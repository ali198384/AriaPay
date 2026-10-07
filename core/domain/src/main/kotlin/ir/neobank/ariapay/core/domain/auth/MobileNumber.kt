package ir.neobank.ariapay.core.domain.auth

import ir.neobank.ariapay.core.common.util.FinancialInputNormalizer

@JvmInline
value class MobileNumber private constructor(val value: String) {
    val masked: String get() = "•".repeat(7) + value.takeLast(4)

    companion object {
        fun parse(raw: String): MobileNumber? {
            val normalized = FinancialInputNormalizer.normalizeMobileNumber(raw) ?: return null
            return MobileNumber(normalized)
        }
    }

    override fun toString(): String = "MobileNumber($masked)"
}
