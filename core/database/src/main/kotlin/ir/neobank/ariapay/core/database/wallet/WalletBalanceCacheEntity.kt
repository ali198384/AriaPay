package ir.neobank.ariapay.core.database.wallet

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ir.neobank.ariapay.core.model.CacheSyncStatus
import kotlin.time.Instant

/** User-scoped wallet cache. userId scopes local data; walletId is assigned by the backend. */
@Entity(tableName = "wallet_balance_cache")
data class WalletBalanceCacheEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "wallet_id")
    val walletId: String?,
    @ColumnInfo(name = "available_balance_rials")
    val availableBalanceRials: Long?,
    @ColumnInfo(name = "last_successful_sync_at_epoch_millis")
    val lastSuccessfulSyncAt: Instant?,
    @ColumnInfo(name = "sync_status")
    val syncStatus: CacheSyncStatus
)
