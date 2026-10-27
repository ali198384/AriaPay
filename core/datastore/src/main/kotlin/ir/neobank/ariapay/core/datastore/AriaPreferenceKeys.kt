package ir.neobank.ariapay.core.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

internal object AriaPreferenceKeys {
    val loggedIn = booleanPreferencesKey("logged_in")
    val encryptedAccessToken = stringPreferencesKey("access_token_tink_v1")
    val themeMode = stringPreferencesKey("theme_mode")
}
