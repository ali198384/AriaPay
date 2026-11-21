package ir.neobank.ariapay.feature.wallet.data.remote

import ir.neobank.ariapay.core.common.result.AriaResult
import ir.neobank.ariapay.core.model.Money
import ir.neobank.ariapay.feature.wallet.domain.model.WalletBalance

/** Demo backend response used until a real wallet API is connected. */
class FakeWalletRemoteDataSource : WalletRemoteDataSource {
    override suspend fun fetchBalance(userId: String): AriaResult<WalletBalance> {
        require(userId.isNotBlank())
        return AriaResult.Success(
            WalletBalance(
                walletId = DEMO_WALLET_ID,
                availableBalance = Money.rials(DEMO_BALANCE_RIALS),
            )
        )
    }

    private companion object {
        const val DEMO_WALLET_ID = "demo-wallet-001"
        const val DEMO_BALANCE_RIALS = 84_350_000L
    }
}
