package ir.neobank.ariapay.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ir.neobank.ariapay.core.designsystem.theme.AriaTheme

enum class AriaButtonStyle {
    Primary,
    Secondary,
    Text,
    Destructive,
}

enum class AriaButtonSize {
    Medium,
    Large,
}

@Composable
fun AriaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: AriaButtonStyle = AriaButtonStyle.Primary,
    size: AriaButtonSize = AriaButtonSize.Large,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: ImageVector? = null,
) {
    val clickable = enabled && !loading
    val minHeight = if (size == AriaButtonSize.Large) 56.dp else 48.dp
    val contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
    val sizedModifier = modifier.heightIn(min = minHeight)

    when (style) {
        AriaButtonStyle.Primary -> Button(
            onClick = onClick,
            modifier = sizedModifier,
            enabled = clickable,
            shape = MaterialTheme.shapes.small,
            contentPadding = contentPadding,
        ) {
            AriaButtonContent(
                text = text,
                loading = loading,
                leadingIcon = leadingIcon,
                progressColor = MaterialTheme.colorScheme.onPrimary,
            )
        }

        AriaButtonStyle.Secondary -> OutlinedButton(
            onClick = onClick,
            modifier = sizedModifier,
            enabled = clickable,
            shape = MaterialTheme.shapes.small,
            contentPadding = contentPadding,
        ) {
            AriaButtonContent(
                text = text,
                loading = loading,
                leadingIcon = leadingIcon,
                progressColor = MaterialTheme.colorScheme.primary,
            )
        }

        AriaButtonStyle.Text -> TextButton(
            onClick = onClick,
            modifier = sizedModifier,
            enabled = clickable,
            shape = MaterialTheme.shapes.small,
            contentPadding = contentPadding,
        ) {
            AriaButtonContent(
                text = text,
                loading = loading,
                leadingIcon = leadingIcon,
                progressColor = MaterialTheme.colorScheme.primary,
            )
        }

        AriaButtonStyle.Destructive -> Button(
            onClick = onClick,
            modifier = sizedModifier,
            enabled = clickable,
            shape = MaterialTheme.shapes.small,
            contentPadding = contentPadding,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError,
                disabledContainerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.38f),
                disabledContentColor = MaterialTheme.colorScheme.onError.copy(alpha = 0.38f),
            ),
        ) {
            AriaButtonContent(
                text = text,
                loading = loading,
                leadingIcon = leadingIcon,
                progressColor = MaterialTheme.colorScheme.onError,
            )
        }
    }
}

@Composable
private fun AriaButtonContent(
    text: String,
    loading: Boolean,
    leadingIcon: ImageVector?,
    progressColor: Color,
) {
    if (loading) {
        CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            strokeWidth = 2.dp,
            color = progressColor,
        )
        Spacer(Modifier.width(8.dp))
    } else if (leadingIcon != null) {
        Icon(
            imageVector = leadingIcon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(8.dp))
    }
    Text(text = text, style = MaterialTheme.typography.labelLarge)
}

@Preview(showBackground = true, locale = "fa")
@Preview(
    showBackground = true,
    name = "Dark",
    locale = "fa",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun AriaButtonPreview() {
    AriaTheme {
        Surface {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AriaButton(text = "ورود", onClick = {})
                AriaButton(text = "در حال بررسی", onClick = {}, loading = true)
                AriaButton(text = "انصراف", onClick = {}, style = AriaButtonStyle.Secondary)
                AriaButton(text = "حذف کارت", onClick = {}, style = AriaButtonStyle.Destructive)
            }
        }
    }
}
