package ir.neobank.ariapay.feature.auth.contract

import androidx.compose.runtime.Immutable
import ir.neobank.ariapay.core.common.util.FinancialInputNormalizer
import ir.neobank.ariapay.core.domain.auth.AuthError

/** Form state deliberately excludes the countdown, which has its own StateFlow. */
@Immutable
data class OtpUiState(
    val mobileNumber: String,
    val otpCode: String,
    val isOtpInputEnabled: Boolean,
    val isVerifying: Boolean,
    val isResending: Boolean,
    val canVerify: Boolean,
    val isCodeError: Boolean,
    val errorMessage: String?,
    val infoMessage: String?,
) {
    val isLoading: Boolean
        get() = isVerifying || isResending
}

internal fun AuthUiState.toOtpUiState(
    mapError: (AuthError) -> String,
) = OtpUiState(
    mobileNumber = mobileNumber,
    otpCode = otpCode,
    isOtpInputEnabled = isOtpInputEnabled,
    isVerifying = operation == AuthOperation.VERIFY_OTP,
    isResending = operation == AuthOperation.REQUEST_OTP,
    canVerify = isOtpInputEnabled && !isLoading &&
        FinancialInputNormalizer.normalizeOtpCode(otpCode) != null,
    isCodeError = error == AuthError.InvalidOtp ||
        error == AuthError.WrongCode ||
        error == AuthError.Expired,
    errorMessage = error?.let(mapError),
    infoMessage = infoMessage
)
