package ir.neobank.ariapay.core.common.settings

/** Theme preference that can be shared by the application shell and feature modules. */
enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK;

    fun useDarkTheme(systemIsDark: Boolean): Boolean = when (this) {
        SYSTEM -> systemIsDark
        LIGHT -> false
        DARK -> true
    }
}
