package ir.neobank.ariapay.feature.profile.domain

/** Profile data exposed to the rest of the app, independent of Room and network DTOs. */
data class UserProfile(
    val userId: String,
    val fullName: String?,
    val mobileNumberMasked: String?,
    val nationalCodeMasked: String?,
    val email: String?,
    val profilePhotoPath: String?,
    val kycLevel: String,
    val kycStatus: String,
    val accountStatus: String,
    val serverUpdatedAtEpochMillis: Long?,
) {
    init {
        require(userId.isNotBlank()) { "User ID must not be blank" }
    }
}
