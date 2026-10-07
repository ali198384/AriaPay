package ir.neobank.ariapay.feature.auth.contract

sealed interface AuthEffect {
    data class ShowMessage(val message: String) : AuthEffect
    data object NavigateToOtp : AuthEffect
    data object NavigateToHome : AuthEffect
}
