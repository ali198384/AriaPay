package ir.neobank.ariapay.core.datastore.di


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.neobank.ariapay.core.datastore.AppPinDataSource
import ir.neobank.ariapay.core.datastore.AppPinDataSourceImpl
import ir.neobank.ariapay.core.datastore.AriaPreferencesDataSource
import ir.neobank.ariapay.core.datastore.AriaPreferencesDataSourceImpl
import ir.neobank.ariapay.core.security.SecureValueCipher
import ir.neobank.ariapay.core.security.TinkStringCipher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DataStoreModule {

    @Provides
    @Singleton
    fun provideSecureValueCipher(
        @ApplicationContext context: Context,
    ): SecureValueCipher = TinkStringCipher(context)

    @Provides
    @Singleton
    fun providePreferences(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = PreferenceDataStoreFactory.create(
        produceFile = { context.preferencesDataStoreFile("aria_session") }
    )

    @Provides
    @Singleton
    fun provideAriaPreferencesDataSource(
        dataStore: DataStore<Preferences>,
        cipher: SecureValueCipher,
    ): AriaPreferencesDataSource = AriaPreferencesDataSourceImpl(dataStore, cipher)

    @Provides
    @Singleton
    fun provideAppPinDataSource(
        dataStore: DataStore<Preferences>,
        cipher: SecureValueCipher,
    ): AppPinDataSource = AppPinDataSourceImpl(dataStore, cipher)
}
