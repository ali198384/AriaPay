package ir.neobank.ariapay.feature.wallet.domain

import com.google.common.truth.Truth.assertThat
import ir.neobank.ariapay.core.common.result.AriaException
import ir.neobank.ariapay.core.common.result.AriaResult
import ir.neobank.ariapay.core.model.CacheSyncStatus
import ir.neobank.ariapay.feature.wallet.domain.model.WalletSnapshot
import ir.neobank.ariapay.feature.wallet.domain.repository.WalletRepository
import ir.neobank.ariapay.feature.wallet.domain.usecase.IsWalletCacheStaleUseCase
import ir.neobank.ariapay.feature.wallet.domain.usecase.ObserveWalletUseCase
import ir.neobank.ariapay.feature.wallet.domain.usecase.RefreshWalletUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

class WalletUseCasesTest {

    @Test
    fun `observe returns the empty local snapshot when no balance is cached`() = runTest {
        val repository = FakeWalletRepository()

        val snapshot = ObserveWalletUseCase(repository)("  $TEST_USER_ID  ").first()

        assertThat(snapshot.balance).isNull()
        assertThat(snapshot.lastSuccessfulSyncAt).isNull()
        assertThat(snapshot.syncStatus).isEqualTo(CacheSyncStatus.NEVER_SYNCED)
        assertThat(repository.observedUserId).isEqualTo(TEST_USER_ID)
    }

    @Test
    fun `cache is fresh while its last successful sync is younger than five minutes`() {
        val now = Instant.parse("2026-11-21T12:00:00Z")
        val useCase = IsWalletCacheStaleUseCase(
            clock = FixedClock(now)
        )
        val snapshot = walletSnapshot(lastSuccessfulSyncAt = now - 4.minutes)

        assertThat(useCase(snapshot)).isFalse()
    }

    @Test
    fun `cache is stale when no successful sync exists or max age has elapsed`() {
        val now = Instant.parse("2026-11-21T12:00:00Z")
        val useCase = IsWalletCacheStaleUseCase(
            clock = FixedClock(now)
        )

        assertThat(useCase(walletSnapshot(lastSuccessfulSyncAt = null))).isTrue()
        assertThat(useCase(walletSnapshot(lastSuccessfulSyncAt = now - 5.minutes))).isTrue()
    }

    @Test
    fun `refresh propagates the repository sync error`() = runTest {
        val expectedError = AriaException.NoInternet()
        val repository = FakeWalletRepository(refreshResult = AriaResult.Error(expectedError))

        val result = RefreshWalletUseCase(repository)(" $TEST_USER_ID ")

        assertThat(result).isInstanceOf(AriaResult.Error::class.java)
        assertThat(repository.refreshedUserId).isEqualTo(TEST_USER_ID)
        assertThat((result as AriaResult.Error).exception).isSameInstanceAs(expectedError)
    }

    private class FakeWalletRepository(
        private val refreshResult: AriaResult<Unit> = AriaResult.Success(Unit)
    ) : WalletRepository {
        private val localSnapshot = MutableStateFlow(walletSnapshot(lastSuccessfulSyncAt = null))
        var observedUserId: String? = null
        var refreshedUserId: String? = null

        override fun observeWallet(userId: String): Flow<WalletSnapshot> {
            observedUserId = userId
            return localSnapshot
        }

        override suspend fun refreshWallet(userId: String): AriaResult<Unit> {
            refreshedUserId = userId
            return refreshResult
        }
    }

    private class FixedClock(private val instant: Instant) : Clock {
        override fun now(): Instant = instant
    }

    private companion object {
        const val TEST_USER_ID = "server-user-42"
    }
}

private fun walletSnapshot(lastSuccessfulSyncAt: Instant?) = WalletSnapshot(
    balance = null,
    lastSuccessfulSyncAt = lastSuccessfulSyncAt,
    syncStatus = if (lastSuccessfulSyncAt == null) {
        CacheSyncStatus.NEVER_SYNCED
    } else {
        CacheSyncStatus.SUCCEEDED
    }
)
