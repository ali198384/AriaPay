package ir.neobank.ariapay.feature.auth

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.neobank.ariapay.core.designsystem.component.AriaButton
import ir.neobank.ariapay.core.designsystem.component.AriaFieldType
import ir.neobank.ariapay.core.designsystem.component.AriaTextField
import ir.neobank.ariapay.core.designsystem.adaptive.WindowWidthClass
import ir.neobank.ariapay.core.designsystem.adaptive.rememberWindowWidthClass
import ir.neobank.ariapay.core.designsystem.theme.AriaTheme
import ir.neobank.ariapay.feature.auth.contract.AuthEvent

@Composable
fun MobileNumberRoute(viewModel: AuthViewModel = hiltViewModel()) {
    val widthClass = rememberWindowWidthClass()
    val state by viewModel.mobileNumberUiState.collectAsStateWithLifecycle()
    val onMobileNumberChange: (String) -> Unit = remember(viewModel) {
        { value -> viewModel.onEvent(AuthEvent.MobileNumberChanged(value)) }
    }
    val onContinue: () -> Unit = remember(viewModel) {
        { viewModel.onEvent(AuthEvent.ContinueClicked) }
    }

    MobileNumberScreen(
        mobileNumber = state.mobileNumber,
        isLoading = state.isLoading,
        errorMessage = state.errorMessage,
        onMobileNumberChange = onMobileNumberChange,
        onContinue = onContinue,
        widthClass = widthClass,
    )
}

@Composable
fun MobileNumberScreen(
    mobileNumber: String,
    isLoading: Boolean,
    errorMessage: String? = null,
    onMobileNumberChange: (String) -> Unit,
    onContinue: () -> Unit,
    widthClass: WindowWidthClass = WindowWidthClass.COMPACT,
    modifier: Modifier = Modifier,
) {
    val keyboardActions = remember(onContinue) {
        KeyboardActions(onDone = { onContinue() })
    }

    when (widthClass) {
        WindowWidthClass.COMPACT -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .navigationBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .imePadding(),
            ) {
                WelcomeHeader()
                MobileNumberForm(
                    mobileNumber = mobileNumber,
                    isLoading = isLoading,
                    errorMessage = errorMessage,
                    onMobileNumberChange = onMobileNumberChange,
                    onContinue = onContinue,
                    keyboardActions = keyboardActions,
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-26).dp)
                        .padding(horizontal = 20.dp),
                )
            }
        }

        WindowWidthClass.MEDIUM -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .navigationBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                WelcomeHeader(modifier = Modifier.height(248.dp))
                MobileNumberForm(
                    mobileNumber = mobileNumber,
                    isLoading = isLoading,
                    errorMessage = errorMessage,
                    onMobileNumberChange = onMobileNumberChange,
                    onContinue = onContinue,
                    keyboardActions = keyboardActions,
                    modifier = Modifier
                        .widthIn(max = 600.dp)
                        .fillMaxWidth()
                        .offset(y = (-24).dp)
                        .padding(horizontal = 32.dp),
                )
            }
        }

        WindowWidthClass.EXPANDED -> {
            Row(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .navigationBarsPadding(),
            ) {
                WelcomeHeader(
                    modifier = Modifier
                        .weight(0.9f)
                        .fillMaxHeight(),
                    isExpanded = true,
                )
                Column(
                    modifier = Modifier
                        .weight(1.1f)
                        .fillMaxHeight()
                        .statusBarsPadding()
                        .imePadding()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 48.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    MobileNumberForm(
                        mobileNumber = mobileNumber,
                        isLoading = isLoading,
                        errorMessage = errorMessage,
                        onMobileNumberChange = onMobileNumberChange,
                        onContinue = onContinue,
                        keyboardActions = keyboardActions,
                        modifier = Modifier
                            .widthIn(max = 560.dp)
                            .fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun MobileNumberForm(
    mobileNumber: String,
    isLoading: Boolean,
    errorMessage: String?,
    onMobileNumberChange: (String) -> Unit,
    onContinue: () -> Unit,
    keyboardActions: KeyboardActions,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f),
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "ورود یا ساخت حساب",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "برای دریافت کد تأیید، شماره موبایلت را وارد کن.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                AriaTextField(
                    value = mobileNumber,
                    onValueChange = onMobileNumberChange,
                    label = "شماره موبایل",
                    type = AriaFieldType.Phone,
                    placeholder = "۶۷۸۹ ۳۴۵ ۰۹۱۲",
                    supportingText = errorMessage ?: "شماره موبایل همراه با صفر اول",
                    isError = errorMessage != null,
                    enabled = !isLoading,
                    imeAction = ImeAction.Done,
                    keyboardActions = keyboardActions,
                )

                AriaButton(
                    text = "ادامه",
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth(),
                    loading = isLoading,
                    enabled = !isLoading,
                )

                Text(
                    text = "با ادامه، کد یک‌بارمصرف برای این شماره ارسال می‌شود.",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }

        SecurityNote()

        Text(
            text = "آریاپی؛ همراه مالی روزمره‌ات",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun WelcomeHeader(
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(282.dp),
    isExpanded: Boolean = false,
) {
    val primary = MaterialTheme.colorScheme.primary
    val onPrimary = MaterialTheme.colorScheme.onPrimary
    val headerBrush = remember(primary) {
        Brush.verticalGradient(
            colors = listOf(
                primary,
                primary.copy(alpha = 0.88f),
            ),
        )
    }

    Box(
        modifier = modifier.background(headerBrush),
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawCircle(
                color = onPrimary.copy(alpha = 0.055f),
                radius = size.width * 0.55f,
                center = Offset(size.width * 0.04f, size.height * 0.98f),
            )
            drawCircle(
                color = onPrimary.copy(alpha = 0.045f),
                radius = size.width * 0.34f,
                center = Offset(size.width * 0.04f, size.height * 0.98f),
            )
        }

        Column(
            modifier = Modifier
                .align(if (isExpanded) Alignment.CenterStart else Alignment.TopStart)
                .fillMaxWidth()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = if (isExpanded) 40.dp else 24.dp)
                .padding(top = if (isExpanded) 0.dp else 12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(17.dp))
                        .background(onPrimary.copy(alpha = 0.12f))
                        .border(
                            width = 1.dp,
                            color = onPrimary.copy(alpha = 0.18f),
                            shape = RoundedCornerShape(17.dp),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "آ",
                        color = onPrimary,
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 27.sp,
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                    Text(
                        text = "آریاپی",
                        color = onPrimary,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = "همراه مالی روزمره‌ات",
                        color = onPrimary.copy(alpha = 0.76f),
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }

            Spacer(Modifier.height(27.dp))

            Surface(
                color = onPrimary.copy(alpha = 0.11f),
                contentColor = onPrimary,
                shape = CircleShape,
            ) {
                Text(
                    text = "ساده، سریع، مطمئن",
                    modifier = Modifier.padding(horizontal = 13.dp, vertical = 7.dp),
                    style = MaterialTheme.typography.labelSmall,
                )
            }

            Spacer(Modifier.height(13.dp))

            Text(
                text = "پرداخت‌های روزمره،\nساده‌تر از همیشه",
                color = onPrimary,
                style = MaterialTheme.typography.headlineMedium,
                lineHeight = 36.sp,
            )
        }
    }
}

@Composable
private fun SecurityNote() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f),
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(AriaTheme.extra.gold),
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = "کد یک‌بارمصرف را با هیچ‌کس به‌اشتراک نگذار.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(name = "Compact", showBackground = true, locale = "fa", widthDp = 390, heightDp = 844)
@Composable
private fun MobileNumberCompactPreview() {
    MobileNumberPreview(widthClass = WindowWidthClass.COMPACT)
}

@Preview(name = "Medium", showBackground = true, locale = "fa", widthDp = 700, heightDp = 900)
@Composable
private fun MobileNumberMediumPreview() {
    MobileNumberPreview(widthClass = WindowWidthClass.MEDIUM)
}

@Preview(name = "Expanded", showBackground = true, locale = "fa", widthDp = 1280, heightDp = 800)
@Composable
private fun MobileNumberExpandedPreview() {
    MobileNumberPreview(widthClass = WindowWidthClass.EXPANDED)
}

@Composable
private fun MobileNumberPreview(widthClass: WindowWidthClass) {
    AriaTheme {
        MobileNumberScreen(
            mobileNumber = "09123456789",
            isLoading = false,
            onMobileNumberChange = {},
            onContinue = {},
            widthClass = widthClass,
        )
    }
}
