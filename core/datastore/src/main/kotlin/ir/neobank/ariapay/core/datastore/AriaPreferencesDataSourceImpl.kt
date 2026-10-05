package ir.neobank.ariapay.core.datastore


import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

internal class AriaPreferencesDataSourceImpl(
    private val dataStore: DataStore<Preferences>
) : AriaPreferencesDataSource {

    override val isLoggedIn: Flow<Boolean> = dataStore.data
        .safe()
        .map { preferences -> preferences[Keys.loggedIn] ?: false }

    override val accessToken: Flow<String?> = dataStore.data
        .safe()
        .map { preferences -> preferences[Keys.accessToken] }

    override suspend fun saveSession(accessToken: String) {
        require(accessToken.isNotBlank())
        dataStore.edit { preferences ->
            preferences[Keys.accessToken] = accessToken
            preferences[Keys.loggedIn] = true
        }
    }

    override suspend fun setLoggedIn(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.loggedIn] = value
        }
    }

    override suspend fun setAccessToken(token: String?) {
        dataStore.edit { preferences ->
            if (token.isNullOrBlank()) {
                preferences.remove(Keys.accessToken)
            } else {
                preferences[Keys.accessToken] = token
            }
        }
    }

    override suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    private fun Flow<Preferences>.safe(): Flow<Preferences> = catch { error ->
        if (error is IOException) emit(emptyPreferences()) else throw error
    }

    private object Keys {
        val loggedIn = booleanPreferencesKey("logged_in")
        val accessToken = stringPreferencesKey("access_token")
    }
}
