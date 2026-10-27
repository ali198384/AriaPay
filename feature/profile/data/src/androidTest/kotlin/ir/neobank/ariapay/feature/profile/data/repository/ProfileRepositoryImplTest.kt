package ir.neobank.ariapay.feature.profile.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import ir.neobank.ariapay.core.common.settings.AppThemeMode
import ir.neobank.ariapay.core.database.AriaDatabase
import ir.neobank.ariapay.core.datastore.AriaPreferencesDataSource
import ir.neobank.ariapay.feature.profile.domain.model.UserProfile
import ir.neobank.ariapay.feature.profile.domain.ProfilePhotoStorage
import ir.neobank.ariapay.core.session.AppSessionState
import ir.neobank.ariapay.core.session.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileRepositoryImplTest {

    private lateinit var database: AriaDatabase
    private lateinit var preferences: InMemoryPreferences
    private lateinit var sessionScope: CoroutineScope
    private lateinit var sessionManager: SessionManager
    private lateinit var repository: ProfileRepositoryImpl
    private lateinit var photoStorage: InMemoryPhotoStorage

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AriaDatabase::class.java).build()
        preferences = InMemoryPreferences()
        photoStorage = InMemoryPhotoStorage()
        sessionScope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        sessionManager = SessionManager(sessionScope)
        repository = ProfileRepositoryImpl(
            userProfileDao = database.userProfileDao(),
            preferences = preferences,
            sessionManager = sessionManager,
            photoStorage = photoStorage,
        )
    }

    @After
    fun tearDown() {
        database.close()
        sessionScope.cancel()
    }

    @Test
    fun profileCanBeSavedAndReadLocallyWithoutNetwork() {
        runTest {
            repository.saveProfile(testProfile)

            assertThat(repository.getProfile(testProfile.userId)).isEqualTo(testProfile)
            assertThat(repository.observeProfile(testProfile.userId).first())
                .isEqualTo(testProfile)
        }
    }

    @Test
    fun updatingNameChangesTheProfileObservedFromLocalDatabase() {
        runTest {
            repository.saveProfile(testProfile)

            val updated = repository.updateName(testProfile.userId, "نام جدید")

            assertThat(updated).isTrue()
            assertThat(repository.getProfile(testProfile.userId)?.fullName).isEqualTo("نام جدید")
        }
    }

    @Test
    fun updatingProfilePhotoChangesThePathObservedFromLocalDatabase() {
        runTest {
            repository.saveProfile(testProfile)

            val updated = repository.updateProfilePhotoPath(testProfile.userId, "profile-photos/new.webp")

            assertThat(updated).isTrue()
            assertThat(repository.getProfile(testProfile.userId)?.profilePhotoPath)
                .isEqualTo("profile-photos/new.webp")
        }
    }

    @Test
    fun logoutClearsCachedProfileAndStoredSession() {
        runTest {
            repository.saveProfile(testProfile)
            preferences.saveSession("fake-access-token")
            sessionManager.startSession()

            repository.logout()

            assertThat(repository.getProfile(testProfile.userId)).isNull()
            assertThat(preferences.accessToken.first()).isNull()
            assertThat(preferences.isLoggedIn.first()).isFalse()
            assertThat(sessionManager.state.value).isEqualTo(AppSessionState.SignedOut)
            assertThat(photoStorage.wasCleared).isTrue()
        }
    }

    @Test
    fun logoutKeepsCredentialsAndSessionWhenLocalCleanupFails() {
        runTest {
            repository.saveProfile(testProfile)
            preferences.saveSession("fake-access-token")
            sessionManager.startSession()
            photoStorage.failOnDeleteAll = true

            val failure = runCatching { repository.logout() }.exceptionOrNull()

            assertThat(failure).isInstanceOf(IllegalStateException::class.java)
            assertThat(repository.getProfile(testProfile.userId)).isEqualTo(testProfile)
            assertThat(preferences.accessToken.first()).isEqualTo("fake-access-token")
            assertThat(preferences.isLoggedIn.first()).isTrue()
            assertThat(sessionManager.state.value).isEqualTo(AppSessionState.Unlocked)
        }
    }

    private class InMemoryPreferences : AriaPreferencesDataSource {
        private val loggedIn = MutableStateFlow(false)
        private val token = MutableStateFlow<String?>(null)
        private val theme = MutableStateFlow(AppThemeMode.SYSTEM)

        override val isLoggedIn: Flow<Boolean> = loggedIn
        override val accessToken: Flow<String?> = token
        override val themeMode: Flow<AppThemeMode> = theme

        override suspend fun setThemeMode(mode: AppThemeMode) {
            theme.value = mode
        }

        override suspend fun saveSession(accessToken: String) {
            token.value = accessToken
            loggedIn.value = true
        }

        override suspend fun setLoggedIn(value: Boolean) {
            loggedIn.value = value
        }

        override suspend fun setAccessToken(token: String?) {
            this.token.value = token
        }

        override suspend fun clearSession() {
            token.value = null
            loggedIn.value = false
        }
    }

    private class InMemoryPhotoStorage : ProfilePhotoStorage {
        var wasCleared = false
        var failOnDeleteAll = false

        override suspend fun copyFromUri(uri: String): String = "profile-photos/copied.webp"

        override suspend fun delete(path: String) = Unit

        override suspend fun deleteAll() {
            check(!failOnDeleteAll) { "Simulated photo cleanup failure" }
            wasCleared = true
        }
    }

    private companion object {
        val testProfile = UserProfile(
            userId = "server-user-42",
            fullName = "نام آزمایشی",
            mobileNumberMasked = "0912***6789",
            nationalCodeMasked = "00******12",
            email = "user@example.test",
            profilePhotoPath = "profile/avatar.jpg",
            kycLevel = "BASIC",
            kycStatus = "VERIFIED",
            accountStatus = "ACTIVE",
            serverUpdatedAtEpochMillis = 1_800_000_000_000L,
        )
    }
}
