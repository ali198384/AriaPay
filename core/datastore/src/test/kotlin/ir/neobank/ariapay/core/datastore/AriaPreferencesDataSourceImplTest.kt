package ir.neobank.ariapay.core.datastore


import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.google.common.truth.Truth.assertThat
import ir.neobank.ariapay.core.common.settings.AppThemeMode
import ir.neobank.ariapay.core.security.SecureValueCipher
import ir.neobank.ariapay.core.security.SecureValuePurpose
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class AriaPreferencesDataSourceImplTest {

    @TempDir
    lateinit var directory: File

    private data class Fixture(
        val source: AriaPreferencesDataSource,
        val dataStore: DataStore<Preferences>,
    )

    private fun fixture(file: File? = null): Fixture {
        val dataStore = file?.let { backingFile ->
            PreferenceDataStoreFactory.create(produceFile = { backingFile })
        } ?: InMemoryPreferencesDataStore()
        return Fixture(
            source = AriaPreferencesDataSourceImpl(dataStore, TestSecureValueCipher),
            dataStore = dataStore,
        )
    }

    @Test
    fun `پیش‌فرض، خارج از حساب و بدون توکن است`() = runTest {
        val source = fixture().source

        assertThat(source.isLoggedIn.first()).isFalse()
        assertThat(source.accessToken.first()).isNull()
        assertThat(source.themeMode.first()).isEqualTo(AppThemeMode.SYSTEM)
    }

    @Test
    fun `وضعیت ورود ذخیره و دوباره خوانده می‌شود`() = runTest {
        val source = fixture().source

        source.setLoggedIn(true)

        assertThat(source.isLoggedIn.first()).isTrue()
    }

    @Test
    fun `توکن ذخیره و دوباره خوانده می‌شود`() = runTest {
        val source = fixture().source

        source.setAccessToken("GAPGPTMASKTOKENp00mx0yslfpX0X")

        assertThat(source.accessToken.first())
            .isEqualTo("GAPGPTMASKTOKENp00mx0yslfpX0X")
    }

    @Test
    fun `ذخیره نشست، توکن و وضعیت ورود را یکجا ثبت می‌کند`() = runTest {
        val source = fixture().source

        source.saveSession("demo-access")

        assertThat(source.accessToken.first()).isEqualTo("demo-access")
        assertThat(source.isLoggedIn.first()).isTrue()
    }

    @Test
    fun `توکن خالی پاک می‌شود`() = runTest {
        val source = fixture().source
        source.setAccessToken("GAPGPTMASKTOKENp00mx0yslfpX0X")

        source.setAccessToken(" ")

        assertThat(source.accessToken.first()).isNull()
    }

    @Test
    fun `پاک‌کردن نشست هر دو مقدار را برمی‌گرداند`() = runTest {
        val fixture = fixture()
        val source = fixture.source
        source.apply {
            setLoggedIn(true)
            setAccessToken("GAPGPTMASKTOKENp00mx0yslfpX0X")
        }

        source.clearSession()

        assertThat(source.isLoggedIn.first()).isFalse()
        assertThat(source.accessToken.first()).isNull()
    }

    @Test
    fun `انتخاب تم ذخیره می‌شود و بعد از پاک‌کردن نشست باقی می‌ماند`() = runTest {
        val source = fixture().source

        source.setThemeMode(AppThemeMode.DARK)
        source.saveSession("demo-access")
        source.clearSession()

        assertThat(source.themeMode.first()).isEqualTo(AppThemeMode.DARK)
        assertThat(source.isLoggedIn.first()).isFalse()
        assertThat(source.accessToken.first()).isNull()
    }

    @Test
    fun `فایل خراب به‌جای کرش، مقدار پیش‌فرض می‌دهد`() = runTest {
        val file = File(directory, "broken.preferences_pb")
        file.writeText("not-a-preferences-file")
        val source = fixture(file).source

        assertThat(source.isLoggedIn.first()).isFalse()
        assertThat(source.accessToken.first()).isNull()
    }

    @Test
    fun `ذخیره و خواندن توکن از رمزگذار با کاربرد توکن استفاده می‌کند`() = runTest {
        val fixture = fixture()
        val token = "demo-access"

        fixture.source.setAccessToken(token)

        val storedValue = fixture.dataStore.data
            .first()[AriaPreferenceKeys.encryptedAccessToken]
        assertThat(storedValue).isEqualTo("test:ACCESS_TOKEN:$token")
        assertThat(fixture.source.accessToken.first()).isEqualTo(token)
    }

}

/** A deterministic stand-in for storage tests; it does not implement real cryptography. */
private object TestSecureValueCipher : SecureValueCipher {
    override suspend fun encrypt(plaintext: String, purpose: SecureValuePurpose): String =
        "test:${purpose.name}:$plaintext"

    override suspend fun decrypt(ciphertext: String, purpose: SecureValuePurpose): String {
        val prefix = "test:${purpose.name}:"
        check(ciphertext.startsWith(prefix)) { "Ciphertext was created for another purpose" }
        return ciphertext.removePrefix(prefix)
    }
}

private class InMemoryPreferencesDataStore : DataStore<Preferences> {
    private val preferences = MutableStateFlow(emptyPreferences())
    private val mutex = Mutex()

    override val data = preferences

    override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences =
        mutex.withLock {
            transform(preferences.value).also { preferences.value = it }
        }
}
