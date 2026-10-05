package ir.neobank.ariapay.core.data.auth

import ir.neobank.ariapay.core.datastore.AriaPreferencesDataSource
import ir.neobank.ariapay.core.domain.auth.AuthRepository
import ir.neobank.ariapay.core.network.auth.AuthRemoteDataSource

internal class FakeAuthRepository(
    private val remote: AuthRemoteDataSource,
    private val preferences: AriaPreferencesDataSource,
) : AuthRepository by AuthRepositoryImpl(remote, preferences)
