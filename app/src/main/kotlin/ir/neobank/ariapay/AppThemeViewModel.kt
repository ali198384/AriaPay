package ir.neobank.ariapay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.neobank.ariapay.core.common.settings.AppThemeMode
import ir.neobank.ariapay.core.common.settings.AppThemeSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AppThemeViewModel @Inject constructor(
    themeSettings: AppThemeSettings,
) : ViewModel() {
    val themeMode: StateFlow<AppThemeMode> = themeSettings.themeMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AppThemeMode.SYSTEM
    )
}
