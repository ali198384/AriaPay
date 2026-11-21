package ir.neobank.ariapay.core.database.wallet

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletBalanceCacheDao {
    @Query("SELECT * FROM wallet_balance_cache WHERE user_id = :userId LIMIT 1")
    fun observe(userId: String): Flow<WalletBalanceCacheEntity?>

    @Query("SELECT * FROM wallet_balance_cache WHERE user_id = :userId LIMIT 1")
    suspend fun get(userId: String): WalletBalanceCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: WalletBalanceCacheEntity)
}
