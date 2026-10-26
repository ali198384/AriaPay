package ir.neobank.ariapay.feature.auth.data.repository

import ir.neobank.ariapay.core.datastore.AriaPreferencesDataSource
import ir.neobank.ariapay.feature.auth.domain.AuthRepository
import ir.neobank.ariapay.feature.auth.data.remote.AuthRemoteDataSource

internal class FakeAuthRepository(
    private val remote: AuthRemoteDataSource,
    private val preferences: AriaPreferencesDataSource,
) : AuthRepository by AuthRepositoryImpl(remote, preferences)
