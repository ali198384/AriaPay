package ir.neobank.ariapay.core.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.neobank.ariapay.core.data.profile.ProfileRepositoryImpl
import ir.neobank.ariapay.core.database.profile.UserProfileDao
import ir.neobank.ariapay.core.datastore.AriaPreferencesDataSource
import ir.neobank.ariapay.core.domain.profile.ProfileRepository
import ir.neobank.ariapay.core.domain.session.SessionManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object ProfileDataModule {

    @Provides
    @Singleton
    fun provideProfileRepository(
        userProfileDao: UserProfileDao,
        preferences: AriaPreferencesDataSource,
        sessionManager: SessionManager,
    ): ProfileRepository = ProfileRepositoryImpl(
        userProfileDao = userProfileDao,
        preferences = preferences,
        sessionManager = sessionManager
    )
}
