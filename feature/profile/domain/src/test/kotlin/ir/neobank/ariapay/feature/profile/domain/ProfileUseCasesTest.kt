package ir.neobank.ariapay.feature.profile.domain

import com.google.common.truth.Truth.assertThat
import ir.neobank.ariapay.core.common.result.AriaException
import ir.neobank.ariapay.core.common.result.AriaResult
import ir.neobank.ariapay.feature.profile.domain.model.UserProfile
import ir.neobank.ariapay.feature.profile.domain.repository.ProfileRepository
import ir.neobank.ariapay.feature.profile.domain.usecase.UpdateNameUseCase
import ir.neobank.ariapay.feature.profile.domain.usecase.UpdateProfilePhotoUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class ProfileUseCasesTest {

    @Test
    fun `ویرایش نام ورودی را trim می‌کند و مقدار به مخزن می‌رسد`() = runTest {
        val repository = FakeProfileRepository()

        val result = UpdateNameUseCase(repository)(TEST_USER_ID, "  نام تازه  ")

        assertThat(result).isEqualTo(AriaResult.Success(Unit))
        assertThat(repository.profile?.fullName).isEqualTo("نام تازه")
    }

    @Test
    fun `نام خالی را پیش از فراخوانی مخزن رد می‌کند`() = runTest {
        val repository = FakeProfileRepository()

        val result = UpdateNameUseCase(repository)(TEST_USER_ID, "  ")

        assertThat(result).isInstanceOf(AriaResult.Error::class.java)
        assertThat((result as AriaResult.Error).exception).isInstanceOf(AriaException.Validation::class.java)
        assertThat(repository.updateNameCallCount).isEqualTo(0)
    }

    @Test
    fun `عکس جدید ثبت می‌شود و تصویر قدیمی پاک می‌شود`() = runTest {
        val repository = FakeProfileRepository()
        val storage = FakePhotoStorage()

        val result = UpdateProfilePhotoUseCase(repository, storage)(TEST_USER_ID, "content://selected/photo")

        assertThat(result).isEqualTo(AriaResult.Success(Unit))
        assertThat(repository.profile?.profilePhotoPath).isEqualTo(NEW_PHOTO_PATH)
        assertThat(storage.copiedUris).containsExactly("content://selected/photo")
        assertThat(storage.deletedPaths).containsExactly(OLD_PHOTO_PATH)
    }

    @Test
    fun `اگر پروفایل پیدا نشود تصویری کپی نمی‌شود`() = runTest {
        val repository = FakeProfileRepository().apply { profile = null }
        val storage = FakePhotoStorage()

        val result = UpdateProfilePhotoUseCase(repository, storage)(TEST_USER_ID, "content://selected/photo")

        assertThat(result).isInstanceOf(AriaResult.Error::class.java)
        assertThat(storage.copiedUris).isEmpty()
    }

    @Test
    fun `اگر ذخیره مسیر عکس شکست بخورد عکس کپی‌شده پاک می‌شود`() = runTest {
        val repository = FakeProfileRepository().apply { canUpdatePhoto = false }
        val storage = FakePhotoStorage()

        val result = UpdateProfilePhotoUseCase(repository, storage)(TEST_USER_ID, "content://selected/photo")

        assertThat(result).isInstanceOf(AriaResult.Error::class.java)
        assertThat(storage.deletedPaths).containsExactly(NEW_PHOTO_PATH)
        assertThat(repository.profile?.profilePhotoPath).isEqualTo(OLD_PHOTO_PATH)
    }

    private class FakeProfileRepository : ProfileRepository {
        var profile: UserProfile? = UserProfile(
            userId = TEST_USER_ID,
            fullName = "نام قبلی",
            mobileNumberMasked = null,
            nationalCodeMasked = null,
            email = null,
            profilePhotoPath = OLD_PHOTO_PATH,
            kycLevel = "BASIC",
            kycStatus = "NOT_STARTED",
            accountStatus = "ACTIVE",
            serverUpdatedAtEpochMillis = null
        )
        var updateNameCallCount = 0
        var canUpdatePhoto = true

        override fun observeProfile(userId: String): Flow<UserProfile?> = flowOf(profile)
        override suspend fun getProfile(userId: String): UserProfile? = profile?.takeIf { it.userId == userId }
        override suspend fun saveProfile(profile: UserProfile) { this.profile = profile }
        override suspend fun updateName(userId: String, fullName: String): Boolean {
            updateNameCallCount++
            val current = profile?.takeIf { it.userId == userId } ?: return false
            profile = current.copy(fullName = fullName)
            return true
        }
        override suspend fun updateProfilePhotoPath(userId: String, path: String): Boolean {
            val current = profile?.takeIf { it.userId == userId } ?: return false
            if (!canUpdatePhoto) return false
            profile = current.copy(profilePhotoPath = path)
            return true
        }
        override suspend fun logout() { profile = null }
    }

    private class FakePhotoStorage : ProfilePhotoStorage {
        val copiedUris = mutableListOf<String>()
        val deletedPaths = mutableListOf<String>()

        override suspend fun copyFromUri(uri: String): String {
            copiedUris += uri
            return NEW_PHOTO_PATH
        }
        override suspend fun delete(path: String) { deletedPaths += path }
        override suspend fun deleteAll() = Unit
    }

    private companion object {
        const val TEST_USER_ID = "server-user-42"
        const val OLD_PHOTO_PATH = "/data/user/0/app/files/profile-photos/old.jpg"
        const val NEW_PHOTO_PATH = "/data/user/0/app/files/profile-photos/new.webp"
    }
}
