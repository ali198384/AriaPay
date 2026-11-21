package ir.neobank.ariapay.core.model

/** Last completed synchronization result for a locally cached feature snapshot. */
enum class CacheSyncStatus {
    NEVER_SYNCED,
    SUCCEEDED,
    FAILED
}
