package ir.neobank.ariapay.core.database.cards

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ir.neobank.ariapay.core.model.CacheSyncStatus
import kotlin.time.Instant

/** User-scoped metadata for the card-list cache. */
@Entity(tableName = "cards_cache_metadata")
data class CardsCacheMetadataEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "last_successful_sync_at_epoch_millis")
    val lastSuccessfulSyncAt: Instant?,
    @ColumnInfo(name = "sync_status")
    val syncStatus: CacheSyncStatus
)
