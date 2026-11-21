package ir.neobank.ariapay.feature.cards.domain.model

/** Card data safe for app features: a mock reference, BIN and last four digits only. */
data class PaymentCard(
    val token: CardDisplayToken,
    val bin: CardBin,
    val lastFourDigits: CardLastFourDigits,
    val cardholderName: String?,
    val isDefault: Boolean
)
