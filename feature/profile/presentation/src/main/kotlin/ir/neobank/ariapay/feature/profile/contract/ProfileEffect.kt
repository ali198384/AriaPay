package ir.neobank.ariapay.feature.profile.contract

sealed interface ProfileEffect {
    data object NavigateToAuthentication : ProfileEffect
}
