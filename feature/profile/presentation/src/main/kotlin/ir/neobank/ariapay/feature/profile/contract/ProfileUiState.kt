package ir.neobank.ariapay.feature.profile.contract

import ir.neobank.ariapay.feature.profile.domain.UserProfile

data class ProfileUiState(
    val isLoading: Boolean = true,
    val profile: UserProfile? = null,
    val error: ProfileUiError? = null,
) {
    val isEmpty: Boolean
        get() = !isLoading && profile == null && error == null
}

enum class ProfileUiError {
    AUTHENTICATION_REQUIRED,
    PROFILE_LOAD_FAILED,
}
