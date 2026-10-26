package ir.neobank.ariapay.feature.applock.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.google.common.truth.Truth.assertThat
import ir.neobank.ariapay.core.security.SecureValueCipher
import ir.neobank.ariapay.core.security.SecureValuePurpose
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class AppPinRepositoryImplTest {

    @TempDir
    lateinit var directory: File

    @Test
    fun pinIsPurposeEncryptedVerifiedAndCanBeCleared() = runTest {
        val dataStore: DataStore<Preferences> = PreferenceDataStoreFactory.create(
            produceFile = { File(directory, "app_pin.preferences_pb") },
        )
        val repository = AppPinRepositoryImpl(dataStore, TestSecureValueCipher)

        repository.savePin("123456")

        val storedValue = dataStore.data.first()[AppPinPreferenceKeys.encryptedPin]
        assertThat(storedValue).isEqualTo("test:APP_PIN:123456")
        assertThat(repository.hasPin.first()).isTrue()
        assertThat(repository.verifyPin("123456")).isTrue()
        assertThat(repository.verifyPin("654321")).isFalse()

        repository.clearPin()

        assertThat(repository.hasPin.first()).isFalse()
        assertThat(repository.verifyPin("123456")).isFalse()
    }
}

private object TestSecureValueCipher : SecureValueCipher {
    override suspend fun encrypt(plaintext: String, purpose: SecureValuePurpose): String =
        "test:${purpose.name}:$plaintext"

    override suspend fun decrypt(ciphertext: String, purpose: SecureValuePurpose): String {
        val prefix = "test:${purpose.name}:"
        check(ciphertext.startsWith(prefix)) { "Ciphertext was created for another purpose" }
        return ciphertext.removePrefix(prefix)
    }
}
