package ir.neobank.ariapay.core.database.profile

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Local cache of a backend profile. [userId] is the server-issued stable ID;
 * KYC and account status fields keep backend codes for mapping in the domain layer.
 */
@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "full_name")
    val fullName: String?,
    @ColumnInfo(name = "mobile_number_masked")
    val mobileNumberMasked: String?,
    @ColumnInfo(name = "national_code_masked")
    val nationalCodeMasked: String?,
    @ColumnInfo(name = "email")
    val email: String?,
    @ColumnInfo(name = "profile_photo_path")
    val profilePhotoPath: String?,
    @ColumnInfo(name = "kyc_level")
    val kycLevel: String,
    @ColumnInfo(name = "kyc_status")
    val kycStatus: String,
    @ColumnInfo(name = "account_status")
    val accountStatus: String,
    @ColumnInfo(name = "server_updated_at_epoch_millis")
    val serverUpdatedAtEpochMillis: Long?
)
