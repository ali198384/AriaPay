package ir.neobank.ariapay.core.data.di


import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.neobank.ariapay.core.data.auth.AuthRepositoryImpl
import ir.neobank.ariapay.core.datastore.AriaPreferencesDataSource
import ir.neobank.ariapay.core.domain.auth.AuthRepository
import ir.neobank.ariapay.core.network.auth.AuthRemoteDataSource
import ir.neobank.ariapay.core.network.auth.FakeAuthRemoteDataSourceImpl
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
