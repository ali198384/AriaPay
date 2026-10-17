package ir.neobank.ariapay.core.security

/**
 * Best-effort signals about the app and device at the time of inspection.
 * These signals do not prove that a device is safe or compromised.
 */
data class DeviceSecurityStatus(
    val isRooted: Boolean,
    val isDebuggableBuild: Boolean,
    val isDebuggerConnected: Boolean,
)
