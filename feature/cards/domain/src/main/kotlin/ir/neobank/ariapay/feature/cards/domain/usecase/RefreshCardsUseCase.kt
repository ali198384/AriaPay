package ir.neobank.ariapay.feature.cards.domain.usecase

import ir.neobank.ariapay.core.common.result.AriaResult
import ir.neobank.ariapay.feature.cards.domain.repository.CardsRepository

class RefreshCardsUseCase(
    private val repository: CardsRepository
) {
    suspend operator fun invoke(userId: String): AriaResult<Unit> =
        repository.refreshCards(userId.requireValidUserId())
}
