package ir.neobank.ariapay.feature.wallet.data.repository

import ir.neobank.ariapay.core.common.result.AriaResult
import ir.neobank.ariapay.core.database.wallet.WalletBalanceCacheDao
import ir.neobank.ariapay.core.database.wallet.WalletBalanceCacheEntity
import ir.neobank.ariapay.core.model.CacheSyncStatus
import ir.neobank.ariapay.core.model.Money
import ir.neobank.ariapay.feature.wallet.data.remote.WalletRemoteDataSource
import ir.neobank.ariapay.feature.wallet.domain.model.WalletBalance
import ir.neobank.ariapay.feature.wallet.domain.model.WalletSnapshot
import ir.neobank.ariapay.feature.wallet.domain.repository.WalletRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlin.time.Clock

class WalletRepositoryImpl(
    private val cacheDao: WalletBalanceCacheDao,
    private val remoteDataSource: WalletRemoteDataSource,
    private val clock: Clock = Clock.System
) : WalletRepository {
    private val refreshMutex = Mutex()

    override fun observeWallet(userId: String): Flow<WalletSnapshot> {
        val normalizedUserId = userId.requireValidUserId()
        return cacheDao.observe(normalizedUserId)
            .map { entity -> entity?.toSnapshot() ?: EMPTY_SNAPSHOT }
    }

    override suspend fun refreshWallet(userId: String): AriaResult<Unit> {
        val normalizedUserId = userId.requireValidUserId()
        refreshMutex.lock()
        return try {
            when (val result = remoteDataSource.fetchBalance(normalizedUserId)) {
                is AriaResult.Success -> {
                    val balance = result.data
                    cacheDao.upsert(
                        WalletBalanceCacheEntity(
                            userId = normalizedUserId,
                            walletId = balance.walletId,
                            availableBalanceRials = balance.availableBalance.amountInRials,
                            lastSuccessfulSyncAt = clock.now(),
                            syncStatus = CacheSyncStatus.SUCCEEDED,
                        ),
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
        val current = cacheDao.get(userId)
        cacheDao.upsert(
            current?.copy(syncStatus = CacheSyncStatus.FAILED)
                ?: WalletBalanceCacheEntity(
                    userId = userId,
                    walletId = null,
                    availableBalanceRials = null,
                    lastSuccessfulSyncAt = null,
                    syncStatus = CacheSyncStatus.FAILED,
                ),
        )
    }

    private fun String.requireValidUserId(): String = trim().also {
        require(it.isNotEmpty()) { "User ID must not be blank" }
    }

    private fun WalletBalanceCacheEntity.toSnapshot(): WalletSnapshot {
        val balance = if (walletId != null && availableBalanceRials != null) {
            WalletBalance(walletId!!, Money.rials(availableBalanceRials!!))
        } else {
            null
        }
        return WalletSnapshot(
            balance = balance,
            lastSuccessfulSyncAt = lastSuccessfulSyncAt,
            syncStatus = syncStatus,
        )
    }

    private companion object {
        val EMPTY_SNAPSHOT = WalletSnapshot(
            balance = null,
            lastSuccessfulSyncAt = null,
            syncStatus = CacheSyncStatus.NEVER_SYNCED,
        )
    }
}
