package ir.neobank.ariapay.feature.auth.contract

import ir.neobank.ariapay.core.domain.auth.AuthError

data class AuthUiState(
    val mobileNumber: String = "",
    val otpCode: String = "",
    val step: AuthStep = AuthStep.MOBILE_NUMBER,
    val isLoading: Boolean = false,
    val error: AuthError? = null,
    val infoMessage: String? = null,
    /** ثانیه‌شمار ارسال مجدد؛ 0 = فعال */
    val resendSecondsLeft: Int = 0
)
