package ir.neobank.ariapay.feature.wallet.data.repository

import com.google.common.truth.Truth.assertThat
import ir.neobank.ariapay.core.common.result.AriaException
import ir.neobank.ariapay.core.common.result.AriaResult
import ir.neobank.ariapay.core.database.wallet.WalletBalanceCacheDao
import ir.neobank.ariapay.core.database.wallet.WalletBalanceCacheEntity
import ir.neobank.ariapay.core.model.CacheSyncStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.time.Instant

class WalletRepositoryImplTest {

    @Test
    fun `empty local cache produces an empty never synced snapshot`() = runTest {
        val dao = FakeWalletBalanceCacheDao()
        val repository = WalletRepositoryImpl(
            cacheDao = dao,
            remoteDataSource = { error("Remote source must not be called") }
        )

        val snapshot = repository.observeWallet(TEST_USER_ID).first()

        assertThat(snapshot.balance).isNull()
        assertThat(snapshot.lastSuccessfulSyncAt).isNull()
        assertThat(snapshot.syncStatus).isEqualTo(CacheSyncStatus.NEVER_SYNCED)
    }

    @Test
    fun `sync failure keeps the previous wallet data and marks cache failed`() = runTest {
        val lastSuccessfulSyncAt = Instant.parse("2026-11-21T11:45:00Z")
        val cachedBalanceRials = 1_234_500L
        val dao = FakeWalletBalanceCacheDao(
            initialEntity = WalletBalanceCacheEntity(
                userId = TEST_USER_ID,
                walletId = "wallet-42",
                availableBalanceRials = cachedBalanceRials,
                lastSuccessfulSyncAt = lastSuccessfulSyncAt,
                syncStatus = CacheSyncStatus.SUCCEEDED,
            ),
        )
        val remoteFailure = AriaException.NoInternet()
        val repository = WalletRepositoryImpl(
            cacheDao = dao,
            remoteDataSource = { AriaResult.Error(remoteFailure) }
        )

        val result = repository.refreshWallet(TEST_USER_ID)
        val snapshot = repository.observeWallet(TEST_USER_ID).first()

        assertThat(result).isInstanceOf(AriaResult.Error::class.java)
        assertThat((result as AriaResult.Error).exception).isSameInstanceAs(remoteFailure)
        assertThat(snapshot.balance?.availableBalance?.amountInRials).isEqualTo(cachedBalanceRials)
        assertThat(snapshot.lastSuccessfulSyncAt).isEqualTo(lastSuccessfulSyncAt)
        assertThat(snapshot.syncStatus).isEqualTo(CacheSyncStatus.FAILED)
    }

    private class FakeWalletBalanceCacheDao(
        initialEntity: WalletBalanceCacheEntity? = null
    ) : WalletBalanceCacheDao {
        private val cachedEntity = MutableStateFlow(initialEntity)

        override fun observe(userId: String): Flow<WalletBalanceCacheEntity?> =
            cachedEntity.map { entity -> entity?.takeIf { it.userId == userId } }

        override suspend fun get(userId: String): WalletBalanceCacheEntity? =
            cachedEntity.value?.takeIf { it.userId == userId }

        override suspend fun upsert(entity: WalletBalanceCacheEntity) {
            cachedEntity.value = entity
        }
    }

    private companion object {
        const val TEST_USER_ID = "server-user-42"
    }
}
