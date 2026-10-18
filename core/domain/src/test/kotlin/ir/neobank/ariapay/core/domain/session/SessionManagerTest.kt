package ir.neobank.ariapay.core.domain.session

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class SessionManagerTest {

    @Test
    fun initialStateIsSignedOut() = runTest {
        val sessionManager = createSessionManager(backgroundScope)

        assertThat(sessionManager.state.value).isEqualTo(AppSessionState.SignedOut)
    }

    @Test
    fun startSessionLocksAfterInactivityTimeout() = runTest {
        val sessionManager = createSessionManager(backgroundScope)

        sessionManager.startSession()
        runCurrent()

        assertThat(sessionManager.state.value).isEqualTo(AppSessionState.Unlocked)

        advanceTimeBy(INACTIVITY_TIMEOUT_MILLIS.milliseconds)
        runCurrent()

        assertThat(sessionManager.state.value)
            .isEqualTo(AppSessionState.Locked(SessionLockReason.INACTIVITY_TIMEOUT))
    }


    @Test
    fun userActivityRestartsInactivityTimeout() = runTest {
        val sessionManager = createSessionManager(backgroundScope)

        sessionManager.startSession()
        runCurrent()
        advanceTimeBy((INACTIVITY_TIMEOUT_MILLIS / 2).milliseconds)
        runCurrent()

        sessionManager.onUserActivity()
        runCurrent()

        // The original timeout has passed, but the recent activity postponed locking.
        advanceTimeBy((INACTIVITY_TIMEOUT_MILLIS / 2).milliseconds)
        runCurrent()
        assertThat(sessionManager.state.value).isEqualTo(AppSessionState.Unlocked)

        advanceTimeBy((INACTIVITY_TIMEOUT_MILLIS / 2 - 1).milliseconds)
        runCurrent()
        assertThat(sessionManager.state.value).isEqualTo(AppSessionState.Unlocked)

        advanceTimeBy(1.milliseconds)
        runCurrent()
        assertThat(sessionManager.state.value)
            .isEqualTo(AppSessionState.Locked(SessionLockReason.INACTIVITY_TIMEOUT))
    }

    @Test
    fun appBackgroundLocksImmediatelyAndCancelsInactivityTimer() = runTest {
        val sessionManager = createSessionManager(backgroundScope)

        sessionManager.startSession()
        runCurrent()
        sessionManager.onAppBackgrounded()

        assertThat(sessionManager.state.value)
            .isEqualTo(AppSessionState.Locked(SessionLockReason.APP_BACKGROUND))

        advanceTimeBy(INACTIVITY_TIMEOUT_MILLIS.milliseconds)
        runCurrent()

        assertThat(sessionManager.state.value)
            .isEqualTo(AppSessionState.Locked(SessionLockReason.APP_BACKGROUND))
    }

    @Test
    fun authenticationUnlocksOnlyALockedSession() = runTest {
        val sessionManager = createSessionManager(backgroundScope)

        assertThat(sessionManager.unlockAfterAuthentication()).isFalse()

        sessionManager.startSession()
        assertThat(sessionManager.unlockAfterAuthentication()).isFalse()

        sessionManager.lock()

        assertThat(sessionManager.unlockAfterAuthentication()).isTrue()
        assertThat(sessionManager.state.value).isEqualTo(AppSessionState.Unlocked)
    }

    @Test
    fun checkSessionExpiryUsesInjectedClock() = runTest {
        var currentTimeMillis = 0L
        val sessionManager = createSessionManager(
            scope = backgroundScope,
            nowMillis = { currentTimeMillis }
        )
        sessionManager.startSession()

        currentTimeMillis = INACTIVITY_TIMEOUT_MILLIS - 1
        assertThat(sessionManager.checkSessionExpiry()).isFalse()
        assertThat(sessionManager.state.value).isEqualTo(AppSessionState.Unlocked)

        currentTimeMillis = INACTIVITY_TIMEOUT_MILLIS
        assertThat(sessionManager.checkSessionExpiry()).isTrue()
        assertThat(sessionManager.state.value)
            .isEqualTo(AppSessionState.Locked(SessionLockReason.INACTIVITY_TIMEOUT))
    }

    @Test
    fun endingSessionClearsStateAndPreventsPendingTimeoutFromLockingIt() = runTest {
        val sessionManager = createSessionManager(backgroundScope)

        sessionManager.startSession()
        runCurrent()
        sessionManager.endSession()

        assertThat(sessionManager.state.value).isEqualTo(AppSessionState.SignedOut)

        advanceTimeBy(INACTIVITY_TIMEOUT_MILLIS.milliseconds)
        runCurrent()

        assertThat(sessionManager.state.value).isEqualTo(AppSessionState.SignedOut)
    }

    @Test
    fun inactivityTimeoutMustBePositive() = runTest {
        assertThrows<IllegalArgumentException> {
            createSessionManager(
                scope = backgroundScope,
                inactivityTimeoutMillis = 0
            )
        }
    }

    private fun createSessionManager(
        scope: CoroutineScope,
        inactivityTimeoutMillis: Long = INACTIVITY_TIMEOUT_MILLIS,
        nowMillis: () -> Long = { 0L }
    ) = SessionManager(
        scope = scope,
        inactivityTimeoutMillis = inactivityTimeoutMillis,
        nowMillis = nowMillis,
    )

    private companion object {
        const val INACTIVITY_TIMEOUT_MILLIS = 5_000L
    }
}
