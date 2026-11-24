package ir.neobank.ariapay.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ir.neobank.ariapay.core.database.cards.CachedPaymentCardEntity
import ir.neobank.ariapay.core.database.cards.CardsCacheDao
import ir.neobank.ariapay.core.database.cards.CardsCacheMetadataEntity
import ir.neobank.ariapay.core.database.converter.CacheSyncStatusTypeConverter
import ir.neobank.ariapay.core.database.converter.InstantTypeConverter
import ir.neobank.ariapay.core.database.profile.UserProfileDao
import ir.neobank.ariapay.core.database.profile.UserProfileEntity
import ir.neobank.ariapay.core.database.wallet.WalletBalanceCacheDao
import ir.neobank.ariapay.core.database.wallet.WalletBalanceCacheEntity

@Database(
    entities = [
        UserProfileEntity::class,
        WalletBalanceCacheEntity::class,
        CachedPaymentCardEntity::class,
        CardsCacheMetadataEntity::class,
    ],
    version = 1
)
@TypeConverters(InstantTypeConverter::class, CacheSyncStatusTypeConverter::class)
abstract class AriaDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun walletBalanceCacheDao(): WalletBalanceCacheDao
    abstract fun cardsCacheDao(): CardsCacheDao
}

