package ir.neobank.ariapay.core.database.cards

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface CardsCacheDao {
    @Query("SELECT * FROM cached_payment_cards WHERE user_id = :userId ORDER BY is_default DESC, display_token ASC")
    fun observeCards(userId: String): Flow<List<CachedPaymentCardEntity>>

    @Query("SELECT * FROM cards_cache_metadata WHERE user_id = :userId LIMIT 1")
    fun observeMetadata(userId: String): Flow<CardsCacheMetadataEntity?>

    @Query("SELECT * FROM cards_cache_metadata WHERE user_id = :userId LIMIT 1")
    suspend fun getMetadata(userId: String): CardsCacheMetadataEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCards(cards: List<CachedPaymentCardEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMetadata(metadata: CardsCacheMetadataEntity)

    @Query("DELETE FROM cached_payment_cards WHERE user_id = :userId")
    suspend fun clearCards(userId: String)

    @Transaction
    suspend fun replaceSnapshot(
        userId: String,
        cards: List<CachedPaymentCardEntity>,
        metadata: CardsCacheMetadataEntity
    ) {
        clearCards(userId)
        if (cards.isNotEmpty()) upsertCards(cards)
        upsertMetadata(metadata)
    }
}
