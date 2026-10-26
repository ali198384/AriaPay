package ir.neobank.ariapay.feature.profile.contract

sealed interface ProfileEvent {
    data object RetryClicked : ProfileEvent
}
