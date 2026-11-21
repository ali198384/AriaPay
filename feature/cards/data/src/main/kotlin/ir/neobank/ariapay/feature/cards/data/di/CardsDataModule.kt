package ir.neobank.ariapay.feature.cards.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.neobank.ariapay.core.database.cards.CardsCacheDao
import ir.neobank.ariapay.feature.cards.data.remote.CardsRemoteDataSource
import ir.neobank.ariapay.feature.cards.data.remote.FakeCardsRemoteDataSource
import ir.neobank.ariapay.feature.cards.data.repository.CardsRepositoryImpl
import ir.neobank.ariapay.feature.cards.domain.repository.CardsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object CardsDataModule {
    @Provides
    @Singleton
    fun provideCardsRemoteDataSource(): CardsRemoteDataSource = FakeCardsRemoteDataSource()

    @Provides
    @Singleton
    fun provideCardsRepository(
        cacheDao: CardsCacheDao,
        remoteDataSource: CardsRemoteDataSource
    ): CardsRepository = CardsRepositoryImpl(cacheDao, remoteDataSource)
}
