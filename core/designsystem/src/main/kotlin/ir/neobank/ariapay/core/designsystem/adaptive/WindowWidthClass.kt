package ir.neobank.ariapay.core.designsystem.adaptive

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND

enum class WindowWidthClass {
    COMPACT,
    MEDIUM,
    EXPANDED,
}

@Composable
fun rememberWindowWidthClass(): WindowWidthClass {
    val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass

    return remember(windowSizeClass) {
        when {
            windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_EXPANDED_LOWER_BOUND) ->
                WindowWidthClass.EXPANDED

            windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND) ->
                WindowWidthClass.MEDIUM

            else -> WindowWidthClass.COMPACT
        }
    }
}
