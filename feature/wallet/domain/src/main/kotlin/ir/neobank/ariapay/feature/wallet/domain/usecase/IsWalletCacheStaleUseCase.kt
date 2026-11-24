package ir.neobank.ariapay.feature.wallet.domain.usecase

import ir.neobank.ariapay.feature.wallet.domain.model.WalletSnapshot
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

/** Applies a UI freshness threshold to the time of the last successful wallet sync. */
class IsWalletCacheStaleUseCase(
    private val clock: Clock = Clock.System,
    private val maxAge: Duration = 5.minutes
) {
    init {
        require(maxAge.isPositive()) { "Maximum cache age must be greater than zero" }
    }

    operator fun invoke(snapshot: WalletSnapshot): Boolean {
        val lastSuccessfulSyncAt = snapshot.lastSuccessfulSyncAt ?: return true
        return clock.now() - lastSuccessfulSyncAt >= maxAge
    }
}
