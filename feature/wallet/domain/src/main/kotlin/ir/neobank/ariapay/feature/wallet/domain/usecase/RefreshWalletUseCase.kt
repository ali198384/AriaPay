package ir.neobank.ariapay.feature.wallet.domain.usecase

import ir.neobank.ariapay.core.common.result.AriaResult
import ir.neobank.ariapay.feature.wallet.domain.repository.WalletRepository

class RefreshWalletUseCase(
    private val repository: WalletRepository
) {
    suspend operator fun invoke(userId: String): AriaResult<Unit> =
        repository.refreshWallet(userId.requireValidUserId())
}
