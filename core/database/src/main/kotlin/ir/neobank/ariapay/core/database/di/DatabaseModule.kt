package ir.neobank.ariapay.core.database.di


import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.neobank.ariapay.core.database.AriaDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

    private const val DATABASE_NAME = "ariapay.db"

    @Provides
    @Singleton
    fun provideAriaDatabase(
        @ApplicationContext context: Context
    ): AriaDatabase = Room.databaseBuilder(
        context,
        AriaDatabase::class.java,
        DATABASE_NAME,
    ).build()
}
