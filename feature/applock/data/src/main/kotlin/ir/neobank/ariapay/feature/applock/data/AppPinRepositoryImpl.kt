package ir.neobank.ariapay.feature.applock.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import ir.neobank.ariapay.core.security.SecureValueCipher
import ir.neobank.ariapay.core.security.SecureValuePurpose
import ir.neobank.ariapay.feature.applock.domain.AppPinRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

internal class AppPinRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
    private val cipher: SecureValueCipher
) : AppPinRepository {

    override val hasPin: Flow<Boolean> = dataStore.data
        .safe()
        .map { preferences -> preferences[AppPinPreferenceKeys.encryptedPin] != null }

    override suspend fun savePin(pin: String) {
        require(pin.isNotBlank()) { "PIN must not be blank" }
        val encryptedPin = cipher.encrypt(pin, SecureValuePurpose.APP_PIN)
        dataStore.edit { preferences ->
            preferences[AppPinPreferenceKeys.encryptedPin] = encryptedPin
        }
    }

    override suspend fun verifyPin(candidate: String): Boolean {
        if (candidate.isBlank()) return false

        val encryptedPin = dataStore.data.safe()
            .first()[AppPinPreferenceKeys.encryptedPin]
            ?: return false

        return cipher.decrypt(encryptedPin, SecureValuePurpose.APP_PIN) == candidate
    }

    override suspend fun clearPin() {
        dataStore.edit { preferences ->
            preferences.remove(AppPinPreferenceKeys.encryptedPin)
        }
    }

    private fun Flow<Preferences>.safe(): Flow<Preferences> = catch { error ->
        if (error is IOException) emit(emptyPreferences()) else throw error
    }
}

internal object AppPinPreferenceKeys {
    val encryptedPin = stringPreferencesKey("app_pin_tink_v1")
}
