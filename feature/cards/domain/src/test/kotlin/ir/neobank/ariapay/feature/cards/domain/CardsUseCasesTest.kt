package ir.neobank.ariapay.feature.cards.domain

import com.google.common.truth.Truth.assertThat
import ir.neobank.ariapay.core.common.result.AriaException
import ir.neobank.ariapay.core.common.result.AriaResult
import ir.neobank.ariapay.core.model.CacheSyncStatus
import ir.neobank.ariapay.feature.cards.domain.model.CardsSnapshot
import ir.neobank.ariapay.feature.cards.domain.repository.CardsRepository
import ir.neobank.ariapay.feature.cards.domain.usecase.ObserveCardsUseCase
import ir.neobank.ariapay.feature.cards.domain.usecase.RefreshCardsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class CardsUseCasesTest {

    @Test
    fun `observe returns the local empty card snapshot`() = runTest {
        val repository = FakeCardsRepository()

        val snapshot = ObserveCardsUseCase(repository)("  $TEST_USER_ID  ").first()

        assertThat(snapshot.cards).isEmpty()
        assertThat(snapshot.syncStatus).isEqualTo(CacheSyncStatus.NEVER_SYNCED)
        assertThat(repository.observedUserId).isEqualTo(TEST_USER_ID)
    }

    @Test
    fun `refresh propagates the repository sync error`() = runTest {
        val expectedError = AriaException.NoInternet()
        val repository = FakeCardsRepository(refreshResult = AriaResult.Error(expectedError))

        val result = RefreshCardsUseCase(repository)(" $TEST_USER_ID ")

        assertThat(result).isInstanceOf(AriaResult.Error::class.java)
        assertThat(repository.refreshedUserId).isEqualTo(TEST_USER_ID)
        assertThat((result as AriaResult.Error).exception).isSameInstanceAs(expectedError)
    }

    private class FakeCardsRepository(
        private val refreshResult: AriaResult<Unit> = AriaResult.Success(Unit)
    ) : CardsRepository {
        private val localSnapshot = MutableStateFlow(
            CardsSnapshot(
                cards = emptyList(),
                lastSuccessfulSyncAt = null,
                syncStatus = CacheSyncStatus.NEVER_SYNCED
            )
        )
        var observedUserId: String? = null
        var refreshedUserId: String? = null

        override fun observeCards(userId: String): Flow<CardsSnapshot> {
            observedUserId = userId
            return localSnapshot
        }

        override suspend fun refreshCards(userId: String): AriaResult<Unit> {
            refreshedUserId = userId
            return refreshResult
        }
    }

    private companion object {
        const val TEST_USER_ID = "server-user-42"
    }
}
