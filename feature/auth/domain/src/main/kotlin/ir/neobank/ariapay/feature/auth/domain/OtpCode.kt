package ir.neobank.ariapay.feature.auth.domain

import ir.neobank.ariapay.core.common.util.FinancialInputNormalizer

@JvmInline
value class OtpCode private constructor(val value: String) {
    companion object {
        fun parse(raw: String): OtpCode? {
            val normalized = FinancialInputNormalizer.normalizeOtpCode(raw) ?: return null
            return OtpCode(normalized)
        }
    }

    override fun toString(): String = "OtpCode([REDACTED])"
}
