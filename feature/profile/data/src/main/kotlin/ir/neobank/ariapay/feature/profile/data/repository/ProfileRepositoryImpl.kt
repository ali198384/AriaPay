package ir.neobank.ariapay.feature.profile.data.repository

import ir.neobank.ariapay.core.database.profile.UserProfileDao
import ir.neobank.ariapay.core.database.profile.UserProfileEntity
import ir.neobank.ariapay.core.datastore.AriaPreferencesDataSource
import ir.neobank.ariapay.feature.profile.domain.repository.ProfileRepository
import ir.neobank.ariapay.feature.profile.domain.model.UserProfile
import ir.neobank.ariapay.feature.profile.domain.ProfilePhotoStorage
import ir.neobank.ariapay.core.session.SessionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProfileRepositoryImpl(
    private val userProfileDao: UserProfileDao,
    private val preferences: AriaPreferencesDataSource,
    private val sessionManager: SessionManager,
    private val photoStorage: ProfilePhotoStorage,
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

    override suspend fun updateProfilePhotoPath(userId: String, path: String): Boolean =
        userProfileDao.updateProfilePhotoPath(userId, path) > 0

    override suspend fun logout() {
        photoStorage.deleteAll()
        userProfileDao.deleteAll()
        preferences.clearSession()
        sessionManager.endSession()
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
