package ir.neobank.ariapay.feature.profile.contract

import ir.neobank.ariapay.core.common.settings.AppThemeMode
import ir.neobank.ariapay.feature.profile.domain.model.UserProfile

data class ProfileUiState(
    val isLoading: Boolean = true,
    val profile: UserProfile? = null,
    val error: ProfileUiError? = null,
    val isAuthenticated: Boolean = false,
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val isSavingTheme: Boolean = false,
    val themeError: ProfileActionError? = null,
    val isNameDialogVisible: Boolean = false,
    val nameDraft: String = "",
    val isSavingName: Boolean = false,
    val nameError: ProfileActionError? = null,
    val isUpdatingPhoto: Boolean = false,
    val photoError: ProfileActionError? = null,
    val isLogoutDialogVisible: Boolean = false,
    val isLoggingOut: Boolean = false,
    val logoutError: ProfileActionError? = null,
) {
    val isEmpty: Boolean
        get() = !isLoading && profile == null && error == null
}

enum class ProfileUiError {
    AUTHENTICATION_REQUIRED,
    PROFILE_LOAD_FAILED,
}

enum class ProfileActionError {
    NAME_REQUIRED,
    NAME_SAVE_FAILED,
    PHOTO_SAVE_FAILED,
    THEME_SAVE_FAILED,
    LOGOUT_FAILED,
}
