package ir.neobank.ariapay.core.database.profile

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {

    @Query("SELECT * FROM user_profile WHERE user_id = :userId LIMIT 1")
    fun observeByUserId(userId: String): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE user_id = :userId LIMIT 1")
    suspend fun getByUserId(userId: String): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(profile: UserProfileEntity)

    @Query("UPDATE user_profile SET full_name = :fullName WHERE user_id = :userId")
    suspend fun updateFullName(userId: String, fullName: String): Int

    @Query("DELETE FROM user_profile WHERE user_id = :userId")
    suspend fun deleteByUserId(userId: String): Int
}
