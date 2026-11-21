package ir.neobank.ariapay.feature.cards.domain.model

import ir.neobank.ariapay.core.model.CacheSyncStatus
import kotlin.time.Instant

/** Cached card list and its synchronization metadata; card numbers are never part of this model. */
data class CardsSnapshot(
    val cards: List<PaymentCard>,
    val lastSuccessfulSyncAt: Instant?,
    val syncStatus: CacheSyncStatus,
)
