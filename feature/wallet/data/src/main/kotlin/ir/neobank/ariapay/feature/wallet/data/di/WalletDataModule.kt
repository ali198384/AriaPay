package ir.neobank.ariapay.feature.wallet.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.neobank.ariapay.core.database.wallet.WalletBalanceCacheDao
import ir.neobank.ariapay.feature.wallet.data.remote.FakeWalletRemoteDataSource
import ir.neobank.ariapay.feature.wallet.data.remote.WalletRemoteDataSource
import ir.neobank.ariapay.feature.wallet.data.repository.WalletRepositoryImpl
import ir.neobank.ariapay.feature.wallet.domain.repository.WalletRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object WalletDataModule {
    @Provides
    @Singleton
    fun provideWalletRemoteDataSource(): WalletRemoteDataSource = FakeWalletRemoteDataSource()

    @Provides
    @Singleton
    fun provideWalletRepository(
        cacheDao: WalletBalanceCacheDao,
        remoteDataSource: WalletRemoteDataSource,
    ): WalletRepository = WalletRepositoryImpl(cacheDao, remoteDataSource)
}
