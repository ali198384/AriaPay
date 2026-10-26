package ir.neobank.ariapay.core.session

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

/**
 * Owns the in-memory lock state for an authenticated session.
 *
 * The default inactivity timeout is five minutes and can be overridden by the caller.
 * Call [unlockAfterAuthentication] only after PIN or biometric verification succeeds.
 */
class SessionManager(
    private val scope: CoroutineScope,
    private val inactivityTimeoutMillis: Long = DEFAULT_INACTIVITY_TIMEOUT_MILLIS,
    private val nowMillis: () -> Long = { System.nanoTime() / NANOS_PER_MILLISECOND }
) {
    init {
        require(inactivityTimeoutMillis > 0) {
            "Inactivity timeout must be greater than zero"
        }
    }

    private val stateLock = Any()
    private val _state = MutableStateFlow<AppSessionState>(AppSessionState.SignedOut)

    val state: StateFlow<AppSessionState> = _state.asStateFlow()

    private var lastActivityAtMillis: Long? = null
    private var activityGeneration = 0L
    private var expiryJob: Job? = null

    /** Starts a fresh authenticated session after successful sign-in. */
    fun startSession() = synchronized(stateLock) {
        activityGeneration++
        lastActivityAtMillis = nowMillis()
        _state.value = AppSessionState.Unlocked
        scheduleExpiry()
    }

    /** Locks immediately when the process lifecycle reports that the app entered background. */
    fun onAppBackgrounded() = lock(SessionLockReason.APP_BACKGROUND)

    fun lock(reason: SessionLockReason = SessionLockReason.MANUAL) = synchronized(stateLock) {
        if (_state.value !is AppSessionState.SignedOut) {
            activityGeneration++
            cancelExpiry()
            _state.value = AppSessionState.Locked(reason)
        }
    }

    /**
     * Marks the session unlocked after an external authenticator accepts the user.
     * Returns false unless the session is currently locked.
     */
    fun unlockAfterAuthentication(): Boolean = synchronized(stateLock) {
        if (_state.value !is AppSessionState.Locked) return@synchronized false

        activityGeneration++
        lastActivityAtMillis = nowMillis()
        _state.value = AppSessionState.Unlocked
        scheduleExpiry()
        true
    }

    /** Records activity and locks the session if its inactivity timeout has elapsed. */
    fun onUserActivity() = synchronized(stateLock) {
        if (_state.value !is AppSessionState.Unlocked) return@synchronized

        val now = nowMillis()
        if (hasInactivityTimeoutElapsed(now)) {
            _state.value = AppSessionState.Locked(SessionLockReason.INACTIVITY_TIMEOUT)
            cancelExpiry()
        } else {
            activityGeneration++
            lastActivityAtMillis = now
            scheduleExpiry()
        }
    }

    /**
     * Checks expiry after lifecycle transitions. Returns true when the active session has
     * exceeded the inactivity timeout.
     */
    fun checkSessionExpiry(): Boolean = synchronized(stateLock) {
        if (_state.value is AppSessionState.SignedOut) return@synchronized false

        val expired = hasInactivityTimeoutElapsed(nowMillis())
        if (expired) {
            _state.value = AppSessionState.Locked(SessionLockReason.INACTIVITY_TIMEOUT)
            cancelExpiry()
        }
        expired
    }

    /** Ends the authenticated session, for example after logout. */
    fun endSession() = synchronized(stateLock) {
        activityGeneration++
        cancelExpiry()
        lastActivityAtMillis = null
        _state.value = AppSessionState.SignedOut
    }

    private fun scheduleExpiry() {
        cancelExpiry()
        if (lastActivityAtMillis == null) return
        val scheduledGeneration = activityGeneration

        expiryJob = scope.launch {
            delay(inactivityTimeoutMillis.milliseconds)
            synchronized(stateLock) {
                val activityIsUnchanged = activityGeneration == scheduledGeneration
                if (activityIsUnchanged && _state.value is AppSessionState.Unlocked) {
                    _state.value = AppSessionState.Locked(SessionLockReason.INACTIVITY_TIMEOUT)
                    expiryJob = null
                }
            }
        }
    }

    private fun cancelExpiry() {
        expiryJob?.cancel()
        expiryJob = null
    }

    private fun hasInactivityTimeoutElapsed(now: Long): Boolean {
        val lastActivity = lastActivityAtMillis ?: return false
        return now - lastActivity >= inactivityTimeoutMillis
    }

    private companion object {
        const val DEFAULT_INACTIVITY_TIMEOUT_MILLIS = 5 * 60 * 1_000L
        const val NANOS_PER_MILLISECOND = 1_000_000L
    }
}
