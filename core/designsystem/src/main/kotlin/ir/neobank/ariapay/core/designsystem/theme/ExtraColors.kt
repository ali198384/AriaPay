package ir.neobank.ariapay.core.designsystem.theme


import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class AriaExtraColors(
    val success: Color,
    val onSuccess: Color,
    val successContainer: Color,
    val warning: Color,
    val onWarning: Color,
    val warningContainer: Color,
    val credit: Color,   // + مبلغ
    val debit: Color,    // - مبلغ
    val gold: Color,     // اکسنت برند / وضعیت ویژه
    val scrim: Color,
)

internal val LightExtraColors = AriaExtraColors(
    success = AriaSuccess,
    onSuccess = Color.White,
    successContainer = AriaSuccessContainer,
    warning = AriaWarning,
    onWarning = Color.White,
    warningContainer = AriaWarningContainer,
    credit = AriaCredit,
    debit = AriaDebit,
    gold = AriaGold,
    scrim = Color(0x99000000),
)

internal val DarkExtraColors = AriaExtraColors(
    success = Color(0xFF6FDBA4),
    onSuccess = Color(0xFF00391F),
    successContainer = Color(0xFF0E3B26),
    warning = Color(0xFFFFC56A),
    onWarning = Color(0xFF3F2800),
    warningContainer = Color(0xFF4A3200),
    credit = Color(0xFF6FDBA4),
    debit = Color(0xFFF2B8B5),
    gold = Color(0xFFE0C36A),
    scrim = Color(0xCC000000),
)

internal val LocalAriaExtraColors = staticCompositionLocalOf<AriaExtraColors> {
    error("AriaExtraColors ارائه نشده؛ محتوا باید داخل AriaTheme باشد.")
}
