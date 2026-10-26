package ir.neobank.ariapay.feature.auth.contract

sealed interface AuthEffect {
    data object NavigateToOtp : AuthEffect
    data object NavigateToHome : AuthEffect
}
