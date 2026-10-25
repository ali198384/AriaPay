package ir.neobank.ariapay.core.data.profile

import ir.neobank.ariapay.core.database.profile.UserProfileDao
import ir.neobank.ariapay.core.database.profile.UserProfileEntity
import ir.neobank.ariapay.core.datastore.AriaPreferencesDataSource
import ir.neobank.ariapay.core.domain.profile.ProfileRepository
import ir.neobank.ariapay.core.domain.profile.UserProfile
import ir.neobank.ariapay.core.domain.session.SessionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProfileRepositoryImpl(
    private val userProfileDao: UserProfileDao,
    private val preferences: AriaPreferencesDataSource,
    private val sessionManager: SessionManager,
) : ProfileRepository {

    override fun observeProfile(userId: String): Flow<UserProfile?> =
        userProfileDao.observeByUserId(userId).map { it?.toDomain() }

    override suspend fun getProfile(userId: String): UserProfile? =
        userProfileDao.getByUserId(userId)?.toDomain()

    override suspend fun saveProfile(profile: UserProfile) {
        userProfileDao.upsert(profile.toEntity())
    }

    override suspend fun updateName(userId: String, fullName: String): Boolean =
        userProfileDao.updateFullName(userId, fullName) > 0

    override suspend fun logout() {
        sessionManager.endSession()
        try {
            preferences.clearSession()
        } finally {
            userProfileDao.deleteAll()
        }
    }
}

private fun UserProfileEntity.toDomain() = UserProfile(
    userId = userId,
    fullName = fullName,
    mobileNumberMasked = mobileNumberMasked,
    nationalCodeMasked = nationalCodeMasked,
    email = email,
    profilePhotoPath = profilePhotoPath,
    kycLevel = kycLevel,
    kycStatus = kycStatus,
    accountStatus = accountStatus,
    serverUpdatedAtEpochMillis = serverUpdatedAtEpochMillis,
)

private fun UserProfile.toEntity() = UserProfileEntity(
    userId = userId,
    fullName = fullName,
    mobileNumberMasked = mobileNumberMasked,
    nationalCodeMasked = nationalCodeMasked,
    email = email,
    profilePhotoPath = profilePhotoPath,
    kycLevel = kycLevel,
    kycStatus = kycStatus,
    accountStatus = accountStatus,
    serverUpdatedAtEpochMillis = serverUpdatedAtEpochMillis,
)
