package ir.neobank.ariapay.feature.cards.domain.model

/** The only card-number suffix retained for display. */
@JvmInline
value class CardLastFourDigits(val value: String) {
    init {
        require(value.length == DIGIT_COUNT && value.all { it in '0'..'9' }) {
            "Card suffix must contain exactly four ASCII digits"
        }
    }

    private companion object {
        const val DIGIT_COUNT = 4
    }
}
