package ir.neobank.ariapay.feature.profile.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ir.neobank.ariapay.feature.profile.domain.usecase.GetProfileUseCase
import ir.neobank.ariapay.feature.profile.domain.usecase.LogoutUseCase
import ir.neobank.ariapay.feature.profile.domain.repository.ProfileRepository
import ir.neobank.ariapay.feature.profile.domain.usecase.UpdateNameUseCase
import ir.neobank.ariapay.feature.profile.domain.ProfilePhotoStorage
import ir.neobank.ariapay.feature.profile.domain.usecase.UpdateProfilePhotoUseCase

@Module
@InstallIn(ViewModelComponent::class)
object ProfileUseCaseModule {
    @Provides
    fun provideGetProfileUseCase(repository: ProfileRepository): GetProfileUseCase =
        GetProfileUseCase(repository)

    @Provides
    fun provideUpdateNameUseCase(repository: ProfileRepository): UpdateNameUseCase =
        UpdateNameUseCase(repository)

    @Provides
    fun provideLogoutUseCase(repository: ProfileRepository): LogoutUseCase =
        LogoutUseCase(repository)

    @Provides
    fun provideUpdateProfilePhotoUseCase(
        repository: ProfileRepository,
        photoStorage: ProfilePhotoStorage,
    ): UpdateProfilePhotoUseCase = UpdateProfilePhotoUseCase(repository, photoStorage)
}
