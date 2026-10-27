package ir.neobank.ariapay.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ir.neobank.ariapay.core.designsystem.adaptive.WindowWidthClass
import ir.neobank.ariapay.core.designsystem.adaptive.rememberWindowWidthClass

@Composable
fun HomeRoute(onNavigateToProfile: () -> Unit = {}) {
    HomeScreen(
        widthClass = rememberWindowWidthClass(),
        onNavigateToProfile = onNavigateToProfile,
    )
}

@Composable
fun HomeScreen(
    widthClass: WindowWidthClass = WindowWidthClass.COMPACT,
    onNavigateToProfile: () -> Unit = {},
) {
    val maxContentWidth = when (widthClass) {
        WindowWidthClass.COMPACT -> 480.dp
        WindowWidthClass.MEDIUM -> 640.dp
        WindowWidthClass.EXPANDED -> 800.dp
    }
    val horizontalPadding = when (widthClass) {
        WindowWidthClass.COMPACT -> 20.dp
        WindowWidthClass.MEDIUM -> 40.dp
        WindowWidthClass.EXPANDED -> 64.dp
    }
    val textStyle = when (widthClass) {
        WindowWidthClass.COMPACT -> MaterialTheme.typography.titleLarge
        WindowWidthClass.MEDIUM -> MaterialTheme.typography.headlineMedium
        WindowWidthClass.EXPANDED -> MaterialTheme.typography.displaySmall
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .widthIn(max = maxContentWidth)
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "AriaPay",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            IconButton(onClick = onNavigateToProfile) {
                Icon(
                    imageVector = Icons.Outlined.AccountCircle,
                    contentDescription = "رفتن به پروفایل",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }

        Text(
            text = "AriaPay",
            modifier = Modifier
                .align(Alignment.Center)
                .widthIn(max = maxContentWidth)
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding),
            style = textStyle,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomePreview() {
    HomeScreen()
}
