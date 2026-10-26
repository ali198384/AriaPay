package ir.neobank.ariapay.feature.auth.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import ir.neobank.ariapay.feature.auth.domain.AuthRepository
import ir.neobank.ariapay.feature.auth.domain.RequestOtpUseCase
import ir.neobank.ariapay.feature.auth.domain.VerifyOtpUseCase

@Module
@InstallIn(ViewModelComponent::class)
object AuthUseCaseModule {
    @Provides
    fun provideRequestOtpUseCase(repository: AuthRepository): RequestOtpUseCase =
        RequestOtpUseCase(repository)

    @Provides
    fun provideVerifyOtpUseCase(repository: AuthRepository): VerifyOtpUseCase =
        VerifyOtpUseCase(repository)
}
