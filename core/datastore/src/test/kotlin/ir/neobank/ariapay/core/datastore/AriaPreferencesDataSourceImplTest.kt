package ir.neobank.ariapay.core.datastore


import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class AriaPreferencesDataSourceImplTest {

    @TempDir
    lateinit var directory: File

    private fun dataSource(
        file: File = File(directory, "aria_session.preferences_pb"),
    ): AriaPreferencesDataSource = AriaPreferencesDataSourceImpl(
        PreferenceDataStoreFactory.create(produceFile = { file }),
    )

    @Test
    fun `پیش‌فرض، خارج از حساب و بدون توکن است`() = runTest {
        val source = dataSource()

        assertThat(source.isLoggedIn.first()).isFalse()
        assertThat(source.accessToken.first()).isNull()
    }

    @Test
    fun `وضعیت ورود ذخیره و دوباره خوانده می‌شود`() = runTest {
        val source = dataSource()

        source.setLoggedIn(true)

        assertThat(source.isLoggedIn.first()).isTrue()
    }

    @Test
    fun `توکن ذخیره و دوباره خوانده می‌شود`() = runTest {
        val source = dataSource()

        source.setAccessToken("GAPGPTMASKTOKENp00mx0yslfpX0X")

        assertThat(source.accessToken.first())
            .isEqualTo("GAPGPTMASKTOKENp00mx0yslfpX0X")
    }

    @Test
    fun `ذخیره نشست، توکن و وضعیت ورود را یکجا ثبت می‌کند`() = runTest {
        val source = dataSource()

        source.saveSession("demo-access")

        assertThat(source.accessToken.first()).isEqualTo("demo-access")
        assertThat(source.isLoggedIn.first()).isTrue()
    }

    @Test
    fun `توکن خالی پاک می‌شود`() = runTest {
        val source = dataSource()
        source.setAccessToken("GAPGPTMASKTOKENp00mx0yslfpX0X")

        source.setAccessToken(" ")

        assertThat(source.accessToken.first()).isNull()
    }

    @Test
    fun `پاک‌کردن نشست هر دو مقدار را برمی‌گرداند`() = runTest {
        val source = dataSource()
        source.apply {
            setLoggedIn(true)
            setAccessToken("GAPGPTMASKTOKENp00mx0yslfpX0X")
        }

        source.clearSession()

        assertThat(source.isLoggedIn.first()).isFalse()
        assertThat(source.accessToken.first()).isNull()
    }

    @Test
    fun `فایل خراب به‌جای کرش، مقدار پیش‌فرض می‌دهد`() = runTest {
        val file = File(directory, "broken.preferences_pb")
        file.writeText("not-a-preferences-file")
        val source = dataSource(file)

        assertThat(source.isLoggedIn.first()).isFalse()
        assertThat(source.accessToken.first()).isNull()
    }
}
