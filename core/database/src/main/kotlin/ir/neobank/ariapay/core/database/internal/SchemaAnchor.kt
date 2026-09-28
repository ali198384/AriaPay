package ir.neobank.ariapay.core.database.internal


import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room بدون Entity کامپایل نمی‌شود.
 * این جدول بیزینس نیست و فیچرها نباید آن را بخوانند.
 */
@Entity(tableName = "schema_anchor")
data class SchemaAnchor(
    @PrimaryKey val id: Int = 1,
)
