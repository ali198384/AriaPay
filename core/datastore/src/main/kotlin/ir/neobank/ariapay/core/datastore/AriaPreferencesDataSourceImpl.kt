package ir.neobank.ariapay.core.datastore


import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import ir.neobank.ariapay.core.security.SecureValueCipher
import ir.neobank.ariapay.core.security.SecureValuePurpose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

internal class AriaPreferencesDataSourceImpl(
    private val dataStore: DataStore<Preferences>,
    private val cipher: SecureValueCipher,
) : AriaPreferencesDataSource {

    override val isLoggedIn: Flow<Boolean> = dataStore.data
        .safe()
        .map { preferences -> preferences[AriaPreferenceKeys.loggedIn] ?: false }

    override val accessToken: Flow<String?> = dataStore.data
        .safe()
        .map { preferences ->
            preferences[AriaPreferenceKeys.encryptedAccessToken]?.let { encryptedToken ->
                cipher.decrypt(encryptedToken, SecureValuePurpose.ACCESS_TOKEN)
            }
        }

    override suspend fun saveSession(accessToken: String) {
        require(accessToken.isNotBlank())
        val encryptedToken = cipher.encrypt(accessToken, SecureValuePurpose.ACCESS_TOKEN)
        dataStore.edit { preferences ->
            preferences[AriaPreferenceKeys.encryptedAccessToken] = encryptedToken
            preferences[AriaPreferenceKeys.loggedIn] = true
        }
    }

    override suspend fun setLoggedIn(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[AriaPreferenceKeys.loggedIn] = value
        }
    }

    override suspend fun setAccessToken(token: String?) {
        val encryptedToken = token
            ?.takeUnless(String::isBlank)
            ?.let { cipher.encrypt(it, SecureValuePurpose.ACCESS_TOKEN) }
        dataStore.edit { preferences ->
            if (encryptedToken == null) {
                preferences.remove(AriaPreferenceKeys.encryptedAccessToken)
            } else {
                preferences[AriaPreferenceKeys.encryptedAccessToken] = encryptedToken
            }
        }
    }

    override suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.remove(AriaPreferenceKeys.loggedIn)
            preferences.remove(AriaPreferenceKeys.encryptedAccessToken)
        }
    }

    private fun Flow<Preferences>.safe(): Flow<Preferences> = catch { error ->
        if (error is IOException) emit(emptyPreferences()) else throw error
    }
}
