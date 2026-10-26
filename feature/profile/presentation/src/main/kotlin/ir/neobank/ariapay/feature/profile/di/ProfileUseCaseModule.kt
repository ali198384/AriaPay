package ir.neobank.ariapay.feature.profile.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ir.neobank.ariapay.feature.profile.domain.GetProfileUseCase
import ir.neobank.ariapay.feature.profile.domain.LogoutUseCase
import ir.neobank.ariapay.feature.profile.domain.ProfileRepository
import ir.neobank.ariapay.feature.profile.domain.UpdateNameUseCase

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
}
