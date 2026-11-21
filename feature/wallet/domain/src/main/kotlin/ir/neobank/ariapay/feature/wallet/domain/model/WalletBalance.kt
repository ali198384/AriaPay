package ir.neobank.ariapay.feature.wallet.domain.model

import ir.neobank.ariapay.core.model.Money

/** Available wallet balance; the identifier comes from the account/backend, not a local constant. */
data class WalletBalance(
    val walletId: String,
    val availableBalance: Money
) {
    init {
        require(walletId.isNotBlank()) { "Wallet ID must not be blank" }
    }
}
