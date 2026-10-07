package ir.neobank.ariapay.core.domain.auth

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
