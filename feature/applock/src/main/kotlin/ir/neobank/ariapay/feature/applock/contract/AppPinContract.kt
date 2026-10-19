package ir.neobank.ariapay.feature.applock.contract

import androidx.compose.runtime.Immutable

const val APP_PIN_LENGTH = 4

enum class AppPinMode {
    INITIALIZING,
    CREATE,
    CONFIRM,
    UNLOCK,
    UNAVAILABLE,
}

enum class AppPinError {
    INCORRECT,
    MISMATCH,
    SESSION_UNAVAILABLE,
    STORAGE,
}

@Immutable
data class AppPinUiState(
    val mode: AppPinMode = AppPinMode.INITIALIZING,
    val enteredDigitCount: Int = 0,
    val isLoading: Boolean = false,
    val error: AppPinError? = null,
)

sealed interface AppPinEvent {
    data class DigitPressed(val digit: Char) : AppPinEvent
    data object DeletePressed : AppPinEvent
    data object RetryInitialization : AppPinEvent
    data object ScreenStarted : AppPinEvent
    data object ScreenStopped : AppPinEvent
}

sealed interface AppPinEffect {
    data object PinAccepted : AppPinEffect
}
