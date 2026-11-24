package ir.neobank.ariapay.feature.wallet.domain.usecase

internal fun String.requireValidUserId(): String = trim().also {
    require(it.isNotEmpty()) { "User ID must not be blank" }
}
