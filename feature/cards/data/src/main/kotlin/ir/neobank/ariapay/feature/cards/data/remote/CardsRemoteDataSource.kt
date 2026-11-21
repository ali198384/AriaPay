package ir.neobank.ariapay.feature.cards.data.remote

import ir.neobank.ariapay.core.common.result.AriaResult
import ir.neobank.ariapay.feature.cards.domain.model.PaymentCard

/** Boundary for fetching cards from the backend; replace the fake binding when API is ready. */
fun interface CardsRemoteDataSource {
    suspend fun fetchCards(userId: String): AriaResult<List<PaymentCard>>
}
