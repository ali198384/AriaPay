package ir.neobank.ariapay.feature.applock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.neobank.ariapay.core.datastore.AppPinDataSource
import ir.neobank.ariapay.core.domain.session.AppSessionState
import ir.neobank.ariapay.core.domain.session.SessionManager
import ir.neobank.ariapay.feature.applock.contract.APP_PIN_LENGTH
import ir.neobank.ariapay.feature.applock.contract.AppPinEffect
import ir.neobank.ariapay.feature.applock.contract.AppPinError
import ir.neobank.ariapay.feature.applock.contract.AppPinEvent
import ir.neobank.ariapay.feature.applock.contract.AppPinMode
import ir.neobank.ariapay.feature.applock.contract.AppPinUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppPinViewModel @Inject constructor(
    private val pinDataSource: AppPinDataSource,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppPinUiState())
    val uiState = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<AppPinEffect>(extraBufferCapacity = 1)
    val effect: SharedFlow<AppPinEffect> = _effect.asSharedFlow()

    // Compose receives only a digit count; it never receives the raw PIN.
    private val enteredPin = CharArray(APP_PIN_LENGTH)
    private var enteredDigitCount = 0
    private var pinToConfirm: CharArray? = null
    private var initializationJob: Job? = null
    private var pinOperationJob: Job? = null

    init {
        loadPinMode()
    }

    fun onEvent(event: AppPinEvent) {
        when (event) {
            is AppPinEvent.DigitPressed -> onDigitPressed(event.digit)
            AppPinEvent.DeletePressed -> onDeletePressed()
            AppPinEvent.RetryInitialization -> loadPinMode()
            AppPinEvent.ScreenStarted -> loadPinMode()
            AppPinEvent.ScreenStopped -> onScreenStopped()
            AppPinEvent.BiometricAuthenticated -> onBiometricAuthenticated()
            AppPinEvent.BiometricPromptFailed -> onBiometricPromptFailed()
        }
    }

    private fun loadPinMode() {
        if (_uiState.value.isLoading) return

        _uiState.update {
            it.copy(
                mode = AppPinMode.INITIALIZING,
                enteredDigitCount = 0,
                isLoading = true,
                error = null,
            )
        }
        clearEnteredPin()
        clearPinToConfirm()

        initializationJob = viewModelScope.launch {
            try {
                val hasPin = pinDataSource.hasPin.first()
                _uiState.update {
                    it.copy(
                        mode = if (hasPin) AppPinMode.UNLOCK else AppPinMode.CREATE,
                        isLoading = false,
                        error = null,
                    )
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        mode = AppPinMode.UNAVAILABLE,
                        isLoading = false,
                        error = AppPinError.STORAGE,
                    )
                }
            }
        }
    }

    private fun onDigitPressed(digit: Char) {
        val currentState = _uiState.value
        if (currentState.isLoading || !currentState.mode.acceptsInput || digit !in '0'..'9') return
        if (enteredDigitCount >= APP_PIN_LENGTH) return

        enteredPin[enteredDigitCount] = digit
        enteredDigitCount++
        _uiState.update {
            it.copy(enteredDigitCount = enteredDigitCount, error = null)
        }

        if (enteredDigitCount == APP_PIN_LENGTH) {
            val completedPin = enteredPin.concatToString()
            clearEnteredPin()
            handleCompletedPin(currentState.mode, completedPin)
        }
    }

    private fun onDeletePressed() {
        val currentState = _uiState.value
        if (currentState.isLoading || !currentState.mode.acceptsInput || enteredDigitCount == 0) return

        enteredDigitCount--
        enteredPin[enteredDigitCount] = '\u0000'
        _uiState.update {
            it.copy(enteredDigitCount = enteredDigitCount, error = null)
        }
    }

    private fun handleCompletedPin(mode: AppPinMode, pin: String) {
        when (mode) {
            AppPinMode.CREATE -> {
                clearPinToConfirm()
                pinToConfirm = pin.toCharArray()
                _uiState.update {
                    it.copy(mode = AppPinMode.CONFIRM, enteredDigitCount = 0, error = null)
                }
            }

            AppPinMode.CONFIRM -> confirmNewPin(pin)
            AppPinMode.UNLOCK -> verifyPin(pin)
            AppPinMode.INITIALIZING,
            AppPinMode.UNAVAILABLE,
            -> Unit
        }
    }

    private fun confirmNewPin(pin: String) {
        val originalPin = pinToConfirm
        pinToConfirm = null
        val candidatePin = pin.toCharArray()
        val pinsMatch = originalPin?.contentEquals(candidatePin) == true
        originalPin?.fill('\u0000')
        candidatePin.fill('\u0000')
        if (!pinsMatch) {
            _uiState.update {
                it.copy(
                    mode = AppPinMode.CREATE,
                    enteredDigitCount = 0,
                    error = AppPinError.MISMATCH,
                )
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }
        pinOperationJob = viewModelScope.launch {
            try {
                pinDataSource.savePin(pin)
                sessionManager.startSession()
                _uiState.update {
                    it.copy(enteredDigitCount = 0, isLoading = false, error = null)
                }
                _effect.tryEmit(AppPinEffect.PinAccepted)
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        mode = AppPinMode.CREATE,
                        enteredDigitCount = 0,
                        isLoading = false,
                        error = AppPinError.STORAGE,
                    )
                }
            }
        }
    }

    private fun verifyPin(pin: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        pinOperationJob = viewModelScope.launch {
            try {
                val isCorrect = pinDataSource.verifyPin(pin)
                if (!isCorrect) {
                    showPinError(AppPinError.INCORRECT)
                    return@launch
                }

                acceptAuthenticatedSession()
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                showPinError(AppPinError.STORAGE)
            }
        }
    }

    private fun onBiometricAuthenticated() {
        if (_uiState.value.mode != AppPinMode.UNLOCK || _uiState.value.isLoading) return

        clearEnteredPin()
        acceptAuthenticatedSession()
    }

    private fun onBiometricPromptFailed() {
        if (_uiState.value.mode != AppPinMode.UNLOCK || _uiState.value.isLoading) return

        showPinError(AppPinError.BIOMETRIC_FAILED)
    }

    private fun acceptAuthenticatedSession() {
        val sessionCanContinue = when (sessionManager.state.value) {
            AppSessionState.SignedOut -> false
            AppSessionState.Unlocked -> {
                sessionManager.onUserActivity()
                true
            }

            is AppSessionState.Locked -> sessionManager.unlockAfterAuthentication()
        }
        if (!sessionCanContinue) {
            showPinError(AppPinError.SESSION_UNAVAILABLE)
            return
        }

        _uiState.update {
            it.copy(enteredDigitCount = 0, isLoading = false, error = null)
        }
        _effect.tryEmit(AppPinEffect.PinAccepted)
    }

    private fun showPinError(error: AppPinError) {
        clearEnteredPin()
        _uiState.update {
            it.copy(enteredDigitCount = 0, isLoading = false, error = error)
        }
    }

    private fun onScreenStopped() {
        initializationJob?.cancel()
        initializationJob = null
        pinOperationJob?.cancel()
        pinOperationJob = null
        clearEnteredPin()
        clearPinToConfirm()
        _uiState.update {
            it.copy(
                mode = AppPinMode.INITIALIZING,
                enteredDigitCount = 0,
                isLoading = false,
                error = null,
            )
        }
    }

    private fun clearEnteredPin() {
        enteredPin.fill('\u0000')
        enteredDigitCount = 0
    }

    private fun clearPinToConfirm() {
        pinToConfirm?.fill('\u0000')
        pinToConfirm = null
    }

    override fun onCleared() {
        clearEnteredPin()
        clearPinToConfirm()
    }

    private val AppPinMode.acceptsInput: Boolean
        get() = this == AppPinMode.CREATE || this == AppPinMode.CONFIRM || this == AppPinMode.UNLOCK
}
