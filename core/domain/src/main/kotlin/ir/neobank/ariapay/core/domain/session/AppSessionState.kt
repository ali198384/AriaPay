package ir.neobank.ariapay.core.domain.session

/** Current security state of the authenticated app session. */
sealed interface AppSessionState {
    data object SignedOut : AppSessionState

    data object Unlocked : AppSessionState

    data class Locked(val reason: SessionLockReason) : AppSessionState
}

enum class SessionLockReason {
    APP_BACKGROUND,
    INACTIVITY_TIMEOUT,
    MANUAL
}
