package ir.neobank.ariapay.feature.profile.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import android.content.Context
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.neobank.ariapay.feature.profile.data.repository.ProfileRepositoryImpl
import ir.neobank.ariapay.feature.profile.data.storage.InternalProfilePhotoStorage
import ir.neobank.ariapay.core.database.profile.UserProfileDao
import ir.neobank.ariapay.core.datastore.AriaPreferencesDataSource
import ir.neobank.ariapay.feature.profile.domain.repository.ProfileRepository
import ir.neobank.ariapay.feature.profile.domain.ProfilePhotoStorage
import ir.neobank.ariapay.core.session.SessionManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object ProfileDataModule {

    @Provides
    @Singleton
    fun provideProfilePhotoStorage(
        @ApplicationContext context: Context,
    ): ProfilePhotoStorage = InternalProfilePhotoStorage(context)

    @Provides
    @Singleton
    fun provideProfileRepository(
        userProfileDao: UserProfileDao,
        preferences: AriaPreferencesDataSource,
        sessionManager: SessionManager,
        photoStorage: ProfilePhotoStorage,
    ): ProfileRepository = ProfileRepositoryImpl(
        userProfileDao = userProfileDao,
        preferences = preferences,
        sessionManager = sessionManager,
        photoStorage = photoStorage,
    )
}
