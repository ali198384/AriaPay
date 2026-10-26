package ir.neobank.ariapay.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.neobank.ariapay.core.session.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object SessionModule {

    @Provides
    @Singleton
    fun provideSessionManager(): SessionManager = SessionManager(
        scope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
    )
}
