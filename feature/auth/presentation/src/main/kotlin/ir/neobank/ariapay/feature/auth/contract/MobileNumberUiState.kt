package ir.neobank.ariapay.feature.auth.contract

import ir.neobank.ariapay.feature.auth.domain.AuthError

data class MobileNumberUiState(
    val mobileNumber: String,
    val isLoading: Boolean,
    val errorMessage: String?,
)

internal fun AuthUiState.toMobileNumberUiState(
    mapError: (AuthError) -> String,
) = MobileNumberUiState(
    mobileNumber = mobileNumber,
    isLoading = isLoading,
    errorMessage = error?.let(mapError),
)
