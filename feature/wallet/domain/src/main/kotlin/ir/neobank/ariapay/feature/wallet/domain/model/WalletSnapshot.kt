package ir.neobank.ariapay.feature.wallet.domain.model

import ir.neobank.ariapay.core.model.CacheSyncStatus
import kotlin.time.Instant

/** Cached wallet balance and the metadata needed to explain its freshness offline. */
data class WalletSnapshot(
    val balance: WalletBalance?,
    val lastSuccessfulSyncAt: Instant?,
    val syncStatus: CacheSyncStatus,
)
