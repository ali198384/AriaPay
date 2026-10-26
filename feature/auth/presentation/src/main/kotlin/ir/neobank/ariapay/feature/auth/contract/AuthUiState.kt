package ir.neobank.ariapay.feature.auth.contract

import ir.neobank.ariapay.feature.auth.domain.AuthError

data class AuthUiState(
    val mobileNumber: String = "",
    val otpCode: String = "",
    val isOtpInputEnabled: Boolean = false,
    val step: AuthStep = AuthStep.MOBILE_NUMBER,
    val operation: AuthOperation = AuthOperation.NONE,
    val error: AuthError? = null,
    val infoMessage: String? = null,
) {
    val isLoading: Boolean
        get() = operation != AuthOperation.NONE
}
