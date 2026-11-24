package ir.neobank.ariapay.feature.wallet.domain.usecase

import ir.neobank.ariapay.feature.wallet.domain.model.WalletSnapshot
import ir.neobank.ariapay.feature.wallet.domain.repository.WalletRepository
import kotlinx.coroutines.flow.Flow

class ObserveWalletUseCase(
    private val repository: WalletRepository
) {
    operator fun invoke(userId: String): Flow<WalletSnapshot> =
        repository.observeWallet(userId.requireValidUserId())
}
