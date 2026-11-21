package ir.neobank.ariapay.feature.cards.domain.model

/** Opaque demo/display reference; it is not a payment credential or card number. */
@JvmInline
value class CardDisplayToken(val value: String) {
    init {
        require(value.length in 1..64) { "Card display token must contain 1 to 64 characters" }
        require(value.first().isAsciiLetter()) { "Card display token must start with a letter" }
        require(value.all { it.isAsciiLetterOrDigit() || it == '-' || it == '_' }) {
            "Card display token contains unsupported characters"
        }
        require(value.count { it in '0'..'9' } < 13) {
            "Card display token must not contain a full card number"
        }
    }
}

private fun Char.isAsciiLetter(): Boolean = this in 'A'..'Z' || this in 'a'..'z'

private fun Char.isAsciiLetterOrDigit(): Boolean = isAsciiLetter() || this in '0'..'9'
