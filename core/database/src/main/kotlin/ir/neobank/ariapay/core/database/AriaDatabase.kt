package ir.neobank.ariapay.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ir.neobank.ariapay.core.database.internal.SchemaAnchor

@Database(
    entities = [SchemaAnchor::class],
    version = 1,
)
abstract class AriaDatabase : RoomDatabase() {

}

