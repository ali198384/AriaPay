package ir.neobank.ariapay.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ir.neobank.ariapay.core.database.profile.UserProfileDao
import ir.neobank.ariapay.core.database.profile.UserProfileEntity

@Database(
    entities = [UserProfileEntity::class],
    version = 1,
)
abstract class AriaDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
}

