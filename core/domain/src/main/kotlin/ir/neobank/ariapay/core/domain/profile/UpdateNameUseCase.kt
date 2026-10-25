package ir.neobank.ariapay.core.domain.profile

import ir.neobank.ariapay.core.common.result.AriaException
import ir.neobank.ariapay.core.common.result.AriaResult

class UpdateNameUseCase(
    private val repository: ProfileRepository,
) {
    suspend operator fun invoke(userId: String, fullName: String): AriaResult<Unit> {
        val normalizedUserId = userId.trim()
        if (normalizedUserId.isEmpty()) {
            return AriaResult.Error(
                AriaException.Validation(
                    message = "شناسه کاربر معتبر نیست.",
                    field = "userId",
                ),
            )
        }

        val normalizedName = fullName.trim()
        if (normalizedName.isEmpty()) {
            return AriaResult.Error(
                AriaException.Validation(
                    message = "نام نمی‌تواند خالی باشد.",
                    field = "fullName",
                ),
            )
        }

        return if (repository.updateName(normalizedUserId, normalizedName)) {
            AriaResult.Success(Unit)
        } else {
            AriaResult.Error(AriaException.NotFound())
        }
    }
}
