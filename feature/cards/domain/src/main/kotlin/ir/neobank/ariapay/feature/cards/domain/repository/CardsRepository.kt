package ir.neobank.ariapay.feature.cards.domain.repository

import ir.neobank.ariapay.core.common.result.AriaResult
import ir.neobank.ariapay.feature.cards.domain.model.CardsSnapshot
import kotlinx.coroutines.flow.Flow

interface CardsRepository {
    /** Room-backed source of truth; an empty list can still be a successfully synced result. */
    fun observeCards(userId: String): Flow<CardsSnapshot>

    /** Fetches the latest backend card list and atomically replaces the local cache. */
    suspend fun refreshCards(userId: String): AriaResult<Unit>
}
