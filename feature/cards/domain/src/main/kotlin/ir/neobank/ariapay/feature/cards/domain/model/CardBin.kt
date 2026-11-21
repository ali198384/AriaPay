package ir.neobank.ariapay.feature.cards.domain.model

/** First six digits used for issuer recognition; never contains a full PAN. */
@JvmInline
value class CardBin(val value: String) {
    init {
        require(value.length == BIN_LENGTH && value.all { it in '0'..'9' }) {
            "Card BIN must contain exactly six ASCII digits"
        }
    }

    private companion object {
        const val BIN_LENGTH = 6
    }
}
