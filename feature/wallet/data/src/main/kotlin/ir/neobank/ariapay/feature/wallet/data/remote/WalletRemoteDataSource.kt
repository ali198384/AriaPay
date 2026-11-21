package ir.neobank.ariapay.feature.wallet.data.remote

import ir.neobank.ariapay.core.common.result.AriaResult
import ir.neobank.ariapay.feature.wallet.domain.model.WalletBalance

/** Boundary for fetching a backend wallet balance; replace the fake binding when API is ready. */
fun interface WalletRemoteDataSource {
    suspend fun fetchBalance(userId: String): AriaResult<WalletBalance>
}
