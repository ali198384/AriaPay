package ir.neobank.ariapay.core.datastore


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

class AriaPreferencesDataSourceImplTest {

    @TempDir
    lateinit var directory: File

    private data class Fixture(
        val source: AriaPreferencesDataSource,
        val pinSource: AppPinDataSource,
        val dataStore: DataStore<Preferences>,
    )

    private fun fixture(
        file: File = File(directory, "aria_session.preferences_pb"),
    ): Fixture {
        val dataStore = PreferenceDataStoreFactory.create(produceFile = { file })
        return Fixture(
            source = AriaPreferencesDataSourceImpl(dataStore, TestSecureValueCipher),
            pinSource = AppPinDataSourceImpl(dataStore, TestSecureValueCipher),
            dataStore = dataStore,
        )
    }

    @Test
    fun `پیش‌فرض، خارج از حساب و بدون توکن است`() = runTest {
        val source = fixture().source

        assertThat(source.isLoggedIn.first()).isFalse()
        assertThat(source.accessToken.first()).isNull()
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
        fixture.pinSource.savePin("123456")

        source.clearSession()

        assertThat(source.isLoggedIn.first()).isFalse()
        assertThat(source.accessToken.first()).isNull()
        assertThat(fixture.pinSource.hasPin.first()).isTrue()
        assertThat(fixture.pinSource.verifyPin("123456")).isTrue()
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

    @Test
    fun `پین با کاربرد جدا ذخیره می‌شود و قابل بررسی است`() = runTest {
        val fixture = fixture()

        fixture.pinSource.savePin("123456")

        val storedValue = fixture.dataStore.data
            .first()[AriaPreferenceKeys.encryptedAppPin]
        assertThat(storedValue).isEqualTo("test:APP_PIN:123456")
        assertThat(fixture.pinSource.hasPin.first()).isTrue()
        assertThat(fixture.pinSource.verifyPin("123456")).isTrue()
        assertThat(fixture.pinSource.verifyPin("654321")).isFalse()

        fixture.pinSource.clearPin()
        assertThat(fixture.pinSource.hasPin.first()).isFalse()
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
