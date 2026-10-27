package ir.neobank.ariapay.feature.profile.contract

import ir.neobank.ariapay.core.common.settings.AppThemeMode

sealed interface ProfileEvent {
    data object RetryClicked : ProfileEvent
    data object EditNameClicked : ProfileEvent
    data class NameDraftChanged(val value: String) : ProfileEvent
    data object DismissNameDialog : ProfileEvent
    data object SaveNameClicked : ProfileEvent
    data class ProfilePhotoSelected(val uri: String) : ProfileEvent
    data class ThemeModeSelected(val mode: AppThemeMode) : ProfileEvent
    data object LogoutClicked : ProfileEvent
    data object DismissLogoutDialog : ProfileEvent
    data object ConfirmLogoutClicked : ProfileEvent
}
