package ir.neobank.ariapay.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import ir.neobank.ariapay.core.security.SecureValueCipher
import ir.neobank.ariapay.core.security.SecureValuePurpose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

internal class AppPinDataSourceImpl(
    private val dataStore: DataStore<Preferences>,
    private val cipher: SecureValueCipher
) : AppPinDataSource {

    override val hasPin: Flow<Boolean> = dataStore.data
        .safe()
        .map { preferences -> preferences[AriaPreferenceKeys.encryptedAppPin] != null }

    override suspend fun savePin(pin: String) {
        require(pin.isNotBlank()) { "PIN must not be blank" }
        val encryptedPin = cipher.encrypt(pin, SecureValuePurpose.APP_PIN)
        dataStore.edit { preferences ->
            preferences[AriaPreferenceKeys.encryptedAppPin] = encryptedPin
        }
    }

    override suspend fun verifyPin(candidate: String): Boolean {
        if (candidate.isBlank()) return false

        val encryptedPin = dataStore.data.safe()
            .first()[AriaPreferenceKeys.encryptedAppPin]
            ?: return false

        return cipher.decrypt(encryptedPin, SecureValuePurpose.APP_PIN) == candidate
    }

    override suspend fun clearPin() {
        dataStore.edit { preferences ->
            preferences.remove(AriaPreferenceKeys.encryptedAppPin)
        }
    }

    private fun Flow<Preferences>.safe(): Flow<Preferences> = catch { error ->
        if (error is IOException) emit(emptyPreferences()) else throw error
    }
}
