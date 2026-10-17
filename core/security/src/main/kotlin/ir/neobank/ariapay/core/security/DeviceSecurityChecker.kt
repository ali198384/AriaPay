package ir.neobank.ariapay.core.security

import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Debug

/** Collects device and build security signals without enforcing a policy. */
class DeviceSecurityChecker(
    context: Context,
    private val rootDetector: RootDetector = RootDetector(),
) {
    private val applicationContext = context.applicationContext

    fun inspect(): DeviceSecurityStatus {
        val appFlags = applicationContext.applicationInfo.flags

        return DeviceSecurityStatus(
            isRooted = rootDetector.isRooted(),
            isDebuggableBuild = appFlags and ApplicationInfo.FLAG_DEBUGGABLE != 0,
            isDebuggerConnected = Debug.isDebuggerConnected() || Debug.waitingForDebugger(),
        )
    }
}
