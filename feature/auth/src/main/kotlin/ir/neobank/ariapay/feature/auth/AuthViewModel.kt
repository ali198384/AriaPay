package ir.neobank.ariapay.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.neobank.ariapay.core.domain.auth.AuthError
import ir.neobank.ariapay.core.domain.auth.AuthResult
import ir.neobank.ariapay.core.domain.auth.RequestOtpUseCase
import ir.neobank.ariapay.core.domain.auth.VerifyOtpUseCase
import ir.neobank.ariapay.feature.auth.contract.AuthEffect
import ir.neobank.ariapay.feature.auth.contract.AuthEvent
import ir.neobank.ariapay.feature.auth.contract.AuthStep
import ir.neobank.ariapay.feature.auth.contract.AuthUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val requestOtpUseCase: RequestOtpUseCase,
    private val verifyOtpUseCase: VerifyOtpUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    val mobileNumberUiState: StateFlow<MobileNumberUiState> = uiState
        .map { state -> state.toMobileNumberUiState(::mapError) }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = uiState.value.toMobileNumberUiState(::mapError),
        )

    private val _effect = MutableSharedFlow<AuthEffect>(
        replay = 0,
        extraBufferCapacity = 1,
    )
    val effect: SharedFlow<AuthEffect> = _effect.asSharedFlow()

    private var resendJob: Job? = null


    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.MobileNumberChanged -> updateMobileNumber(event.value)
            is AuthEvent.OtpCodeChanged -> updateOtpCode(event.value)
            AuthEvent.ContinueClicked -> requestOtp(isResend = false)
            AuthEvent.ResendCodeClicked -> requestOtp(isResend = true)
            AuthEvent.VerifyClicked -> verifyOtp()
            AuthEvent.BackFromOtp -> backFromOtp()
            AuthEvent.ClearMessages -> clearMessages()
        }
    }

    private fun updateMobileNumber(value: String) {
        _uiState.update { state ->
            if (state.isLoading || state.step != AuthStep.MOBILE_NUMBER) {
                state
            } else {
                state.copy(mobileNumber = value, error = null, infoMessage = null)
            }
        }
    }

    private fun updateOtpCode(value: String) {
        _uiState.update { state ->
            if (state.isLoading || state.step != AuthStep.OTP) {
                state
            } else {
                state.copy(otpCode = value, error = null, infoMessage = null)
            }
        }
    }

    private fun requestOtp(isResend: Boolean) {
        viewModelScope.launch {
            val currentState = _uiState.value
            val expectedStep = if (isResend) AuthStep.OTP else AuthStep.MOBILE_NUMBER
            if (currentState.isLoading || currentState.step != expectedStep) return@launch

            _uiState.update { it.copy(isLoading = true, error = null, infoMessage = null) }
            when (val result = requestOtpUseCase(currentState.mobileNumber)) {
                is AuthResult.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            step = if (isResend) state.step else AuthStep.OTP,
                            error = null,
                            infoMessage = if (isResend)
                                "کد دوباره ارسال شد"
                            else
                                "کد تأیید ارسال شد"
                        )
                    }
                    startResendCountdown(RESEND_SECONDS)
                    if (!isResend) _effect.tryEmit(AuthEffect.NavigateToOtp)
                }

                is AuthResult.Failure -> {
                    _uiState.update { it.copy(isLoading = false, error = result.error) }
                }
            }
        }
    }

    private fun verifyOtp() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState.isLoading || currentState.step != AuthStep.OTP) return@launch

            _uiState.update { it.copy(isLoading = true, error = null, infoMessage = null) }
            when (val result = verifyOtpUseCase(currentState.mobileNumber, currentState.otpCode)) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(
                            otpCode = "",
                            step = AuthStep.AUTHENTICATED,
                            isLoading = false,
                            error = null,
                        )
                    }
                    _effect.tryEmit(AuthEffect.NavigateToHome)
                }

                is AuthResult.Failure -> {
                    _uiState.update { it.copy(isLoading = false, error = result.error) }
                }
            }
        }
    }

    private fun backFromOtp() {
        resendJob?.cancel()
        _uiState.update {
            it.copy(
                step = AuthStep.MOBILE_NUMBER,
                otpCode = "",
                error = null,
                isLoading = false,
                resendSecondsLeft = 0,
                infoMessage = null,
            )
        }
    }

    private fun clearMessages() {
        _uiState.update {
            it.copy(error = null, infoMessage = null)
        }
    }

    private fun startResendCountdown(seconds: Int) {
        resendJob?.cancel()
        resendJob = viewModelScope.launch {
            for (left in seconds downTo 0) {
                _uiState.update { it.copy(resendSecondsLeft = left) }
                if (left == 0) break
                delay(1_000.milliseconds)
            }
        }
    }

    fun mapError(error: AuthError): String = when (error) {
        // نام enum/objectها را با AuthError خودت هماهنگ کن
        AuthError.InvalidMobile -> "شماره موبایل معتبر نیست"
        AuthError.InvalidOtp -> "کد تأیید نادرست است"
        AuthError.Expired -> "کد منقضی شده؛ دوباره ارسال کنید"
        AuthError.Network -> "خطای شبکه؛ دوباره تلاش کنید"
        AuthError.Unknown -> "خطای غیرمنتظره رخ داد"
        else -> "خطا در احراز هویت"
    }

    override fun onCleared() {
        resendJob?.cancel()
    }

    private companion object {
        const val RESEND_SECONDS = 30
    }
}
