package ir.neobank.ariapay.core.designsystem.theme


import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = AriaBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD4E5F7),
    onPrimaryContainer = AriaBlueDark,
    secondary = AriaGold,
    onSecondary = Color(0xFF2A2100),
    secondaryContainer = Color(0xFFF4E7C2),
    onSecondaryContainer = Color(0xFF241C00),
    tertiary = AriaSuccess,
    onTertiary = Color.White,
    error = AriaError,
    onError = Color.White,
    errorContainer = AriaErrorContainer,
    onErrorContainer = Color(0xFF410E0B),
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnBackground,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF41474D),
    outline = LightOutline,
)

private val DarkColors = darkColorScheme(
    primary = AriaBlueLight,
    onPrimary = AriaBlueDark,
    primaryContainer = AriaBlueDark,
    onPrimaryContainer = Color(0xFFD4E5F7),
    secondary = Color(0xFFE0C36A),
    onSecondary = Color(0xFF2A2100),
    secondaryContainer = AriaGoldDark,
    onSecondaryContainer = Color(0xFFF4E7C2),
    tertiary = Color(0xFF6FDBA4),
    onTertiary = Color(0xFF00391F),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnBackground,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFC2C7CE),
    outline = DarkOutline,
)

@Composable
fun AriaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkColors else LightColors
    val extra = if (darkTheme) DarkExtraColors else LightExtraColors

    CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Rtl,
        LocalAriaExtraColors provides extra,
    ) {
        MaterialTheme(
            colorScheme = colors,
            typography = AriaTypography,
            shapes = AriaShapes,
            content = content,
        )
    }
}

object AriaTheme {
    val extra: AriaExtraColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAriaExtraColors.current

    val amountTextStyle: TextStyle
        @Composable
        @ReadOnlyComposable
        get() = AmountTextStyle
}

@Preview(showBackground = true, name = "Light", locale = "fa")
@Preview(showBackground = true, name = "Dark", locale = "fa", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AriaThemePreview() {
    AriaTheme {
        Surface {
            Column(Modifier.padding(24.dp)) {
                Text("آریاپِی", style = MaterialTheme.typography.headlineMedium)
                Text("کیف پول ریالی", style = MaterialTheme.typography.bodyLarge)
                Text(
                    "۱۲٬۵۰۰٬۰۰۰ ریال",
                    style = AriaTheme.amountTextStyle,
                    color = AriaTheme.extra.credit,
                )
            }
        }
    }
}