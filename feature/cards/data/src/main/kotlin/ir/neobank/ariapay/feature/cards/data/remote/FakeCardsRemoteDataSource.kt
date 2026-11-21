package ir.neobank.ariapay.feature.cards.data.remote

import ir.neobank.ariapay.core.common.result.AriaResult
import ir.neobank.ariapay.feature.cards.domain.model.CardBin
import ir.neobank.ariapay.feature.cards.domain.model.CardDisplayToken
import ir.neobank.ariapay.feature.cards.domain.model.CardLastFourDigits
import ir.neobank.ariapay.feature.cards.domain.model.PaymentCard

/** Demo backend response. Only display-safe values are included; no full PAN is used or stored. */
class FakeCardsRemoteDataSource : CardsRemoteDataSource {
    override suspend fun fetchCards(userId: String): AriaResult<List<PaymentCard>> {
        require(userId.isNotBlank())
        return AriaResult.Success(
            listOf(
                PaymentCard(
                    token = CardDisplayToken("demo-card-001"),
                    bin = CardBin("603799"),
                    lastFourDigits = CardLastFourDigits("1284"),
                    cardholderName = "کاربر نمونه",
                    isDefault = true,
                ),
                PaymentCard(
                    token = CardDisplayToken("demo-card-002"),
                    bin = CardBin("627412"),
                    lastFourDigits = CardLastFourDigits("5072"),
                    cardholderName = "کاربر نمونه",
                    isDefault = false,
                )
            )
        )
    }
}
