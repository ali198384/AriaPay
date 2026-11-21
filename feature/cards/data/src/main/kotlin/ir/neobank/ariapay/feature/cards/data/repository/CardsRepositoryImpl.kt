package ir.neobank.ariapay.feature.cards.data.repository

import ir.neobank.ariapay.core.common.result.AriaResult
import ir.neobank.ariapay.core.database.cards.CachedPaymentCardEntity
import ir.neobank.ariapay.core.database.cards.CardsCacheDao
import ir.neobank.ariapay.core.database.cards.CardsCacheMetadataEntity
import ir.neobank.ariapay.core.model.CacheSyncStatus
import ir.neobank.ariapay.feature.cards.data.remote.CardsRemoteDataSource
import ir.neobank.ariapay.feature.cards.domain.model.CardBin
import ir.neobank.ariapay.feature.cards.domain.model.CardDisplayToken
import ir.neobank.ariapay.feature.cards.domain.model.CardLastFourDigits
import ir.neobank.ariapay.feature.cards.domain.model.CardsSnapshot
import ir.neobank.ariapay.feature.cards.domain.model.PaymentCard
import ir.neobank.ariapay.feature.cards.domain.repository.CardsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.sync.Mutex
import kotlin.time.Clock

class CardsRepositoryImpl(
    private val cacheDao: CardsCacheDao,
    private val remoteDataSource: CardsRemoteDataSource,
    private val clock: Clock = Clock.System
) : CardsRepository {
    private val refreshMutex = Mutex()

    override fun observeCards(userId: String): Flow<CardsSnapshot> {
        val normalizedUserId = userId.requireValidUserId()
        return combine(
            cacheDao.observeCards(normalizedUserId),
            cacheDao.observeMetadata(normalizedUserId),
        ) { entities, metadata ->
            CardsSnapshot(
                cards = entities.map(CachedPaymentCardEntity::toDomain),
                lastSuccessfulSyncAt = metadata?.lastSuccessfulSyncAt,
                syncStatus = metadata?.syncStatus ?: CacheSyncStatus.NEVER_SYNCED
            )
        }
    }

    override suspend fun refreshCards(userId: String): AriaResult<Unit> {
        val normalizedUserId = userId.requireValidUserId()
        refreshMutex.lock()
        return try {
            when (val result = remoteDataSource.fetchCards(normalizedUserId)) {
                is AriaResult.Success -> {
                    val cards = result.data
                    val metadata = CardsCacheMetadataEntity(
                        userId = normalizedUserId,
                        lastSuccessfulSyncAt = clock.now(),
                        syncStatus = CacheSyncStatus.SUCCEEDED,
                    )
                    cacheDao.replaceSnapshot(
                        userId = normalizedUserId,
                        cards = cards.map { it.toEntity(normalizedUserId) },
                        metadata = metadata,
                    )
                    AriaResult.Success(Unit)
                }

                is AriaResult.Error -> {
                    markSyncFailed(normalizedUserId)
                    result
                }
            }
        } finally {
            refreshMutex.unlock()
        }
    }

    private suspend fun markSyncFailed(userId: String) {
        val current = cacheDao.getMetadata(userId)
        cacheDao.upsertMetadata(
            CardsCacheMetadataEntity(
                userId = userId,
                lastSuccessfulSyncAt = current?.lastSuccessfulSyncAt,
                syncStatus = CacheSyncStatus.FAILED,
            )
        )
    }

    private fun String.requireValidUserId(): String = trim().also {
        require(it.isNotEmpty()) { "User ID must not be blank" }
    }
}

private fun CachedPaymentCardEntity.toDomain() = PaymentCard(
    token = CardDisplayToken(displayToken),
    bin = CardBin(bin),
    lastFourDigits = CardLastFourDigits(lastFourDigits),
    cardholderName = cardholderName,
    isDefault = isDefault,
)

private fun PaymentCard.toEntity(userId: String) = CachedPaymentCardEntity(
    userId = userId,
    displayToken = token.value,
    bin = bin.value,
    lastFourDigits = lastFourDigits.value,
    cardholderName = cardholderName,
    isDefault = isDefault,
)

