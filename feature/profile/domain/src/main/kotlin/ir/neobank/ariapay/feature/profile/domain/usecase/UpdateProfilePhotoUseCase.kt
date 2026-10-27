package ir.neobank.ariapay.feature.profile.domain.usecase

import ir.neobank.ariapay.core.common.result.AriaException
import ir.neobank.ariapay.core.common.result.AriaResult
import ir.neobank.ariapay.feature.profile.domain.ProfilePhotoStorage
import ir.neobank.ariapay.feature.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.CancellationException

class UpdateProfilePhotoUseCase(
    private val repository: ProfileRepository,
    private val photoStorage: ProfilePhotoStorage,
) {
    suspend operator fun invoke(userId: String, sourceUri: String): AriaResult<Unit> {
        val normalizedUserId = userId.trim()
        if (normalizedUserId.isEmpty()) {
            return AriaResult.Error(
                AriaException.Validation("شناسهٔ کاربر معتبر نیست.", field = "userId"),
            )
        }
        if (sourceUri.isBlank()) {
            return AriaResult.Error(
                AriaException.Validation("تصویر انتخاب‌شده معتبر نیست.", field = "photo"),
            )
        }

        val profile = repository.getProfile(normalizedUserId)
            ?: return AriaResult.Error(AriaException.NotFound())
        val newPath = try {
            photoStorage.copyFromUri(sourceUri)
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Exception) {
            return AriaResult.Error(
                AriaException.Unknown(error, "ذخیرهٔ تصویر پروفایل انجام نشد."),
            )
        }

        try {
            if (!repository.updateProfilePhotoPath(normalizedUserId, newPath)) {
                photoStorage.delete(newPath)
                return AriaResult.Error(AriaException.NotFound())
            }
        } catch (cancelled: CancellationException) {
            photoStorage.delete(newPath)
            throw cancelled
        } catch (error: Exception) {
            photoStorage.delete(newPath)
            throw error
        }

        profile.profilePhotoPath
            ?.takeIf(String::isNotBlank)
            ?.let { oldPath ->
                try {
                    photoStorage.delete(oldPath)
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    // The new image is already saved; the orphaned old file can be cleaned later.
                }
            }

        return AriaResult.Success(Unit)
    }
}
