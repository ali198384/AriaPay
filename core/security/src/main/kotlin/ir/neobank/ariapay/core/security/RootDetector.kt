package ir.neobank.ariapay.core.security

import java.io.File

/**
 * Looks for common `su` and Superuser installation paths.
 * This is a heuristic and can have false positives or miss a rooted device.
 */
class RootDetector(
    private val indicators: List<File> = DEFAULT_INDICATORS,
) {
    fun isRooted(): Boolean = indicators.any { it.exists() }

    private companion object {
        val DEFAULT_INDICATORS = listOf(
            "/system/app/Superuser.apk",
            "/system/priv-app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/system/xbin/daemonsu",
            "/data/local/su",
            "/data/local/bin/su",
            "/data/local/xbin/su",
            "/su/bin/su",
        ).map(::File)
    }
}
