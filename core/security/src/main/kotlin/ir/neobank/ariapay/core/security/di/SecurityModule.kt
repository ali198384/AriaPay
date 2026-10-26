package ir.neobank.ariapay.core.security.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.neobank.ariapay.core.security.SecureValueCipher
import ir.neobank.ariapay.core.security.TinkStringCipher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object SecurityModule {
    @Provides
    @Singleton
    fun provideSecureValueCipher(
        @ApplicationContext context: Context,
    ): SecureValueCipher = TinkStringCipher(context)
}
