package ir.neobank.ariapay.core.security

import android.view.Window
import android.view.WindowManager

/** Controls Android's screenshot and non-secure-display protection for a window. */
object SecureWindow {
    fun setEnabled(window: Window, enabled: Boolean) {
        if (enabled) {
            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    fun isEnabled(window: Window): Boolean =
        window.attributes.flags and WindowManager.LayoutParams.FLAG_SECURE != 0
}
