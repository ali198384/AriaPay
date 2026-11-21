package ir.neobank.ariapay.core.database.converter

import androidx.room.TypeConverter
import kotlin.time.Instant

/** Stores timestamps as epoch milliseconds while exposing a typed instant in Kotlin. */
class InstantTypeConverter {
    @TypeConverter
    fun instantToEpochMilliseconds(value: Instant?): Long? =
        value?.toEpochMilliseconds()

    @TypeConverter
    fun epochMillisecondsToInstant(value: Long?): Instant? =
        value?.let { Instant.fromEpochMilliseconds(it) }
}
