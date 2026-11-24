package ir.neobank.ariapay.feature.cards.domain.usecase

internal fun String.requireValidUserId(): String = trim().also {
    require(it.isNotEmpty()) { "User ID must not be blank" }
}
