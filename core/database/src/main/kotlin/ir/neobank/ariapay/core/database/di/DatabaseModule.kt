package ir.neobank.ariapay.core.database.di


import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.neobank.ariapay.core.database.AriaDatabase
import ir.neobank.ariapay.core.database.cards.CardsCacheDao
import ir.neobank.ariapay.core.database.profile.UserProfileDao
import ir.neobank.ariapay.core.database.wallet.WalletBalanceCacheDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

    private const val DATABASE_NAME = "ariapay.db"

    @Provides
    @Singleton
    fun provideAriaDatabase(
        @ApplicationContext context: Context
    ): AriaDatabase = Room.databaseBuilder(
        context,
        AriaDatabase::class.java,
        DATABASE_NAME,
    ).build()

    @Provides
    @Singleton
    fun provideUserProfileDao(database: AriaDatabase): UserProfileDao =
        database.userProfileDao()

    @Provides
    @Singleton
    fun provideWalletBalanceCacheDao(database: AriaDatabase): WalletBalanceCacheDao =
        database.walletBalanceCacheDao()

    @Provides
    @Singleton
    fun provideCardsCacheDao(database: AriaDatabase): CardsCacheDao =
        database.cardsCacheDao()
}
