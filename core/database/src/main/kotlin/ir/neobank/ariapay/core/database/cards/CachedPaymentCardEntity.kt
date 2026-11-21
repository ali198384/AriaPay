package ir.neobank.ariapay.core.database.cards

import androidx.room.ColumnInfo
import androidx.room.Entity

/** Safe-to-cache card display data. Full PAN and authentication credentials are excluded. */
@Entity(tableName = "cached_payment_cards", primaryKeys = ["user_id", "display_token"])
data class CachedPaymentCardEntity(
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "display_token")
    val displayToken: String,
    @ColumnInfo(name = "bin")
    val bin: String,
    @ColumnInfo(name = "last_four_digits")
    val lastFourDigits: String,
    @ColumnInfo(name = "cardholder_name")
    val cardholderName: String?,
    @ColumnInfo(name = "is_default")
    val isDefault: Boolean
)
