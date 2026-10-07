package ir.neobank.ariapay.feature.auth.contract

sealed interface AuthEvent {
    data class MobileNumberChanged(val value: String) : AuthEvent
    data class OtpCodeChanged(val value: String) : AuthEvent
    data object ContinueClicked : AuthEvent
    data object ResendCodeClicked : AuthEvent
    data object VerifyClicked : AuthEvent
    data object BackFromOtp : AuthEvent
    data object ClearMessages : AuthEvent
}
