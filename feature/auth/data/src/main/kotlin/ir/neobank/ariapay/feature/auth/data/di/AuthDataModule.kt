package ir.neobank.ariapay.feature.auth.data.di


import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.neobank.ariapay.feature.auth.data.repository.AuthRepositoryImpl
import ir.neobank.ariapay.core.datastore.AriaPreferencesDataSource
import ir.neobank.ariapay.feature.auth.domain.repository.AuthRepository
import ir.neobank.ariapay.feature.auth.data.remote.AuthRemoteDataSource
import ir.neobank.ariapay.feature.auth.data.remote.FakeAuthRemoteDataSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthDataModule {

    @Provides
    @Singleton
    fun provideAuthRemoteDataSource(): AuthRemoteDataSource =
        FakeAuthRemoteDataSourceImpl()

    @Provides
    @Singleton
    fun provideAuthRepository(
        remote: AuthRemoteDataSource,
        preferences: AriaPreferencesDataSource,
    ): AuthRepository = AuthRepositoryImpl(remote, preferences)
}
