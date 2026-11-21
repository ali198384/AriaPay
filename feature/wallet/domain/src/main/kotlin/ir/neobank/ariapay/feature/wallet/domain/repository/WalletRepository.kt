package ir.neobank.ariapay.feature.wallet.domain.repository

import ir.neobank.ariapay.core.common.result.AriaResult
import ir.neobank.ariapay.feature.wallet.domain.model.WalletSnapshot
import kotlinx.coroutines.flow.Flow

interface WalletRepository {
    /** Room-backed source of truth; emits cached data immediately, including when offline. */
    fun observeWallet(userId: String): Flow<WalletSnapshot>

    /** Fetches the latest backend snapshot and atomically replaces the local cache. */
    suspend fun refreshWallet(userId: String): AriaResult<Unit>
}
