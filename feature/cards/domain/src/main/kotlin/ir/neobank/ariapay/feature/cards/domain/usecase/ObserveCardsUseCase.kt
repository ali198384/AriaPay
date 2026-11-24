package ir.neobank.ariapay.feature.cards.domain.usecase

import ir.neobank.ariapay.feature.cards.domain.model.CardsSnapshot
import ir.neobank.ariapay.feature.cards.domain.repository.CardsRepository
import kotlinx.coroutines.flow.Flow

class ObserveCardsUseCase(
    private val repository: CardsRepository
) {
    operator fun invoke(userId: String): Flow<CardsSnapshot> =
        repository.observeCards(userId.requireValidUserId())
}
