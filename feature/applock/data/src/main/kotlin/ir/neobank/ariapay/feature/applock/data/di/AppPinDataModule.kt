package ir.neobank.ariapay.feature.applock.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.neobank.ariapay.core.security.SecureValueCipher
import ir.neobank.ariapay.feature.applock.data.AppPinRepositoryImpl
import ir.neobank.ariapay.feature.applock.domain.AppPinRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AppPinDataModule {
    @Provides
    @Singleton
    fun provideAppPinRepository(
        dataStore: DataStore<Preferences>,
        cipher: SecureValueCipher,
    ): AppPinRepository = AppPinRepositoryImpl(dataStore, cipher)
}
