package ir.neobank.ariapay.core.common.settings

import kotlinx.coroutines.flow.Flow

interface AppThemeSettings {
    val themeMode: Flow<AppThemeMode>

    suspend fun setThemeMode(mode: AppThemeMode)
}
