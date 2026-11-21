package ir.neobank.ariapay.core.database.converter

import androidx.room.TypeConverter
import ir.neobank.ariapay.core.model.CacheSyncStatus

/** Stores the status name and safely handles values unknown to the current app version. */
class CacheSyncStatusTypeConverter {
    @TypeConverter
    fun fromCacheSyncStatus(value: CacheSyncStatus): String = value.name

    @TypeConverter
    fun toCacheSyncStatus(value: String): CacheSyncStatus =
        CacheSyncStatus.entries.firstOrNull { it.name == value } ?: CacheSyncStatus.NEVER_SYNCED
}
