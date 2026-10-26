package ir.neobank.ariapay.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.neobank.ariapay.core.common.util.FinancialInputNormalizer
import ir.neobank.ariapay.feature.auth.domain.AuthError
import ir.neobank.ariapay.feature.auth.domain.AuthResult
import ir.neobank.ariapay.feature.auth.domain.RequestOtpUseCase
import ir.neobank.ariapay.feature.auth.domain.VerifyOtpUseCase
import ir.neobank.ariapay.feature.auth.contract.AuthEffect
import ir.neobank.ariapay.feature.auth.contract.AuthEvent
import ir.neobank.ariapay.feature.auth.contract.AuthOperation
import ir.neobank.ariapay.feature.auth.contract.AuthStep
import ir.neobank.ariapay.feature.auth.contract.AuthUiState
import ir.neobank.ariapay.feature.auth.contract.MobileNumberUiState
import ir.neobank.ariapay.feature.auth.contract.OtpUiState
import ir.neobank.ariapay.feature.auth.contract.toMobileNumberUiState
import ir.neobank.ariapay.feature.auth.contract.toOtpUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

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
            initialValue = uiState.value.toMobileNumberUiState(::mapError)
        )

    val otpUiState: StateFlow<OtpUiState> = uiState
        .map { state -> state.toOtpUiState(::mapError) }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = uiState.value.toOtpUiState(::mapError)
        )

    private val _resendSecondsLeft = MutableStateFlow(0)
    val resendSecondsLeft: StateFlow<Int> = _resendSecondsLeft.asStateFlow()

    private val _effect = Channel<AuthEffect>(capacity = Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var resendJob: Job? = null
    private var otpRequestedForMobileNumber: String? = null

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.MobileNumberChanged -> updateMobileNumber(event.value)
            is AuthEvent.OtpCodeChanged -> updateOtpCode(event.value)
            is AuthEvent.OtpCodeCompleted -> {
                updateOtpCode(event.value)
                verifyOtp()
            }
            AuthEvent.ContinueClicked -> requestOtp(isResend = false)
            AuthEvent.ResendCodeClicked -> requestOtp(isResend = true)
            AuthEvent.VerifyClicked -> verifyOtp()
            AuthEvent.BackFromOtp -> backFromOtp()
            AuthEvent.ClearMessages -> clearMessages()
        }
    }

    private fun updateMobileNumber(value: String) {
        val requestedNumber = otpRequestedForMobileNumber
        val editedNumber = FinancialInputNormalizer.normalizeMobileNumber(value)
        if (requestedNumber != null && editedNumber != requestedNumber) {
            resetOtpSession()
        }
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
            if (state.isLoading || state.step != AuthStep.OTP || !state.isOtpInputEnabled) {
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
            if (isResend && _resendSecondsLeft.value > 0) return@launch

            val normalizedNumber = FinancialInputNormalizer.normalizeMobileNumber(currentState.mobileNumber)
            if (!isResend && normalizedNumber != null &&
                otpRequestedForMobileNumber == normalizedNumber
            ) {
                _uiState.update {
                    it.copy(
                        step = AuthStep.OTP,
                        otpCode = "",
                        error = null,
                        infoMessage = if (it.isOtpInputEnabled) {
                            "کد قبلی هنوز معتبر است"
                        } else {
                            "زمان کد به پایان رسیده؛ برای ادامه کد تازه را ارسال کن"
                        },
                    )
                }
                _effect.trySend(AuthEffect.NavigateToOtp)
                return@launch
            }

            _uiState.update {
                it.copy(operation = AuthOperation.REQUEST_OTP, error = null, infoMessage = null)
            }
            when (val result = requestOtpUseCase(currentState.mobileNumber)) {
                is AuthResult.Success -> {
                    otpRequestedForMobileNumber = normalizedNumber
                    _uiState.update { state ->
                        state.copy(
                            operation = AuthOperation.NONE,
                            step = if (isResend) state.step else AuthStep.OTP,
                            otpCode = "",
                            isOtpInputEnabled = true,
                            error = null,
                            infoMessage = if (isResend)
                                "کد دوباره ارسال شد"
                            else
                                "کد تأیید ارسال شد",
                        )
                    }
                    startResendCountdown()
                    if (!isResend) _effect.trySend(AuthEffect.NavigateToOtp)
                }

                is AuthResult.Failure -> {
                    _uiState.update {
                        it.copy(operation = AuthOperation.NONE, error = result.error)
                    }
                }
            }
        }
    }

    private fun verifyOtp() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState.isLoading || currentState.step != AuthStep.OTP ||
                !currentState.isOtpInputEnabled
            ) return@launch

            _uiState.update {
                it.copy(operation = AuthOperation.VERIFY_OTP, error = null, infoMessage = null)
            }
            when (val result = verifyOtpUseCase(currentState.mobileNumber, currentState.otpCode)) {
                is AuthResult.Success -> {
                    resendJob?.cancel()
                    resendJob = null
                    _uiState.update {
                        it.copy(
                            otpCode = "",
                            step = AuthStep.AUTHENTICATED,
                            operation = AuthOperation.NONE,
                            isOtpInputEnabled = false,
                            error = null,
                        )
                    }
                    _resendSecondsLeft.value = 0
                    otpRequestedForMobileNumber = null
                    _effect.trySend(AuthEffect.NavigateToHome(result.value.userId))
                }

                is AuthResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            otpCode = if (result.error == AuthError.Expired) "" else it.otpCode,
                            operation = AuthOperation.NONE,
                            isOtpInputEnabled = it.isOtpInputEnabled &&
                                result.error != AuthError.Expired,
                            error = result.error,
                        )
                    }
                    if (result.error == AuthError.Expired) {
                        resendJob?.cancel()
                        resendJob = null
                        _resendSecondsLeft.value = 0
                    }
                }
            }
        }
    }

    private fun backFromOtp() {
        val currentState = _uiState.value
        if (currentState.step != AuthStep.OTP || currentState.isLoading) return

        _uiState.update {
            it.copy(
                step = AuthStep.MOBILE_NUMBER,
                otpCode = "",
                error = null,
                operation = AuthOperation.NONE,
                infoMessage = null,
            )
        }
    }

    private fun startResendCountdown(seconds: Int = RESEND_SECONDS) {
        resendJob?.cancel()
        _resendSecondsLeft.value = seconds
        resendJob = viewModelScope.launch {
            for (left in seconds - 1 downTo 0) {
                delay(1.seconds)
                _resendSecondsLeft.value = left
            }
            _uiState.update {
                it.copy(
                    otpCode = "",
                    isOtpInputEnabled = false,
                    error = null,
                    infoMessage = "زمان کد به پایان رسید؛ برای ادامه کد تازه را ارسال کن",
                )
            }
            resendJob = null
        }
    }

    private fun resetOtpSession() {
        resendJob?.cancel()
        resendJob = null
        _resendSecondsLeft.value = 0
        otpRequestedForMobileNumber = null
        _uiState.update {
            it.copy(
                otpCode = "",
                isOtpInputEnabled = false,
                error = null,
                infoMessage = null,
            )
        }
    }

    private fun mapError(error: AuthError): String = when (error) {
        AuthError.InvalidMobile -> "شماره موبایل معتبر نیست"
        AuthError.InvalidOtp -> "کد تأیید باید شش رقم باشد"
        AuthError.WrongCode -> "کد تأیید نادرست است؛ دوباره بررسی کن"
        AuthError.Expired -> "کد منقضی شده؛ دوباره ارسال کنید"
        AuthError.Network -> "خطای شبکه؛ دوباره تلاش کنید"
        AuthError.Unknown -> "خطای غیرمنتظره رخ داد"
    }

    private fun clearMessages() {
        _uiState.update {
            it.copy(error = null, infoMessage = null)
        }
    }

    override fun onCleared() {
        resendJob?.cancel()
    }

    private companion object {
        const val RESEND_SECONDS = 30
    }
}
