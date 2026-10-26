package ir.neobank.ariapay.feature.auth

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.neobank.ariapay.core.designsystem.adaptive.WindowWidthClass
import ir.neobank.ariapay.core.designsystem.adaptive.rememberWindowWidthClass
import ir.neobank.ariapay.core.designsystem.component.AriaButton
import ir.neobank.ariapay.core.designsystem.component.AriaButtonSize
import ir.neobank.ariapay.core.designsystem.component.AriaButtonStyle
import ir.neobank.ariapay.core.designsystem.component.AriaOtpField
import ir.neobank.ariapay.core.designsystem.component.toPersianDigits
import ir.neobank.ariapay.core.designsystem.theme.AriaTheme
import ir.neobank.ariapay.feature.auth.contract.AuthEffect
import ir.neobank.ariapay.feature.auth.contract.AuthEvent
import ir.neobank.ariapay.feature.auth.contract.OtpUiState
import kotlinx.coroutines.flow.StateFlow

@Composable
fun OtpRoute(
    onBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val currentOnNavigateToHome by rememberUpdatedState(onNavigateToHome)
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            if (effect == AuthEffect.NavigateToHome) currentOnNavigateToHome()
        }
    }

    val state by viewModel.otpUiState.collectAsStateWithLifecycle()
    val widthClass = rememberWindowWidthClass()
    val onCodeChange: (String) -> Unit = { value ->
        viewModel.onEvent(AuthEvent.OtpCodeChanged(value))
    }
    val onCodeComplete: (String) -> Unit = { value ->
        viewModel.onEvent(AuthEvent.OtpCodeCompleted(value))
    }
    val onResend: () -> Unit = {
        viewModel.onEvent(AuthEvent.ResendCodeClicked)
    }
    val returnToMobile: () -> Unit = {
        viewModel.onEvent(AuthEvent.BackFromOtp)
        onBack()

    }
    BackHandler {
        if (!state.isLoading) returnToMobile()
    }
    OtpScreen(
        state = state,
        onCodeChange = onCodeChange,
        onCodeComplete = onCodeComplete,
        onEditNumber = returnToMobile,
        resendContent = {
            OtpResendSection(
                secondsLeft = viewModel.resendSecondsLeft,
                isLoading = state.isLoading,
                isResending = state.isResending,
                onResend = onResend,
            )
        },
        widthClass = widthClass,
    )
}

/** Pure UI. The countdown is a slot so its per-second updates stay local to the resend area. */
@Composable
fun OtpScreen(
    state: OtpUiState,
    onCodeChange: (String) -> Unit,
    onCodeComplete: (String) -> Unit,
    onEditNumber: () -> Unit,
    resendContent: @Composable () -> Unit,
    widthClass: WindowWidthClass = WindowWidthClass.COMPACT,
    modifier: Modifier = Modifier,
) {
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
                OtpHero(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(252.dp)
                )
                OtpForm(
                    state = state,
                    onCodeChange = onCodeChange,
                    onCodeComplete = onCodeComplete,
                    onEditNumber = onEditNumber,
                    resendContent = resendContent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-24).dp)
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
                OtpHero(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(248.dp)
                )
                OtpForm(
                    state = state,
                    onCodeChange = onCodeChange,
                    onCodeComplete = onCodeComplete,
                    onEditNumber = onEditNumber,
                    resendContent = resendContent,
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
                OtpHero(
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
                    OtpForm(
                        state = state,
                        onCodeChange = onCodeChange,
                        onCodeComplete = onCodeComplete,
                        onEditNumber = onEditNumber,
                        resendContent = resendContent,
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
private fun OtpHero(
    modifier: Modifier,
    isExpanded: Boolean = false,
) {
    val primary = MaterialTheme.colorScheme.primary
    val onPrimary = MaterialTheme.colorScheme.onPrimary
    val brush = remember(primary) {
        Brush.verticalGradient(listOf(primary, primary.copy(alpha = 0.88f)))
    }
    Box(modifier = modifier.background(brush)) {
        Canvas(Modifier.matchParentSize()) {
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
                        .background(onPrimary.copy(alpha = 0.12f)),
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
                    Text("آریاپی", color = onPrimary, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "همراه مالی روزمره‌ات",
                        color = onPrimary.copy(alpha = 0.76f),
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
            Surface(
                color = onPrimary.copy(alpha = 0.11f), contentColor = onPrimary,
                shape = CircleShape
            ) {
                Text(
                    "تأیید شماره موبایل",
                    modifier = Modifier.padding(horizontal = 13.dp, vertical = 7.dp),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            Spacer(Modifier.height(13.dp))
            Text(
                "یک قدم تا ورود\nبه آریاپی",
                color = onPrimary,
                style = MaterialTheme.typography.headlineMedium,
                lineHeight = 36.sp,
            )
        }
    }
}

@Composable
private fun OtpForm(
    state: OtpUiState,
    onCodeChange: (String) -> Unit,
    onCodeComplete: (String) -> Unit,
    onEditNumber: () -> Unit,
    resendContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    VerificationMark()
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("کد پیامک‌شده را وارد کن", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "برای ادامه، شماره‌ات را تأیید کن.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                OtpRecipient(
                    mobileNumber = state.mobileNumber,
                    enabled = !state.isLoading,
                    onEditNumber = onEditNumber,
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("کد تأیید شش‌رقمی", style = MaterialTheme.typography.labelLarge)
                    AriaOtpField(
                        value = state.otpCode,
                        onValueChange = onCodeChange,
                        onComplete = onCodeComplete,
                        enabled = state.isOtpInputEnabled && !state.isLoading,
                        isError = state.isCodeError,
                        errorMessage = state.errorMessage,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OtpMessage(state.errorMessage, state.infoMessage)
                }
                resendContent()
            }
        }
        SecurityNote()
    }
}

@Composable
private fun VerificationMark() {
    val primary = MaterialTheme.colorScheme.primary
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(25.dp)) {
            drawCircle(color = primary, style = Stroke(width = 1.7.dp.toPx()))
            drawLine(
                primary,
                Offset(size.width * 0.27f, size.height * 0.51f),
                Offset(size.width * 0.44f, size.height * 0.67f),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round,
            )
            drawLine(
                primary,
                Offset(size.width * 0.44f, size.height * 0.67f),
                Offset(size.width * 0.74f, size.height * 0.34f),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round,
            )
        }
    }
}

@Composable
private fun OtpRecipient(mobileNumber: String, enabled: Boolean, onEditNumber: () -> Unit) {
    val displayedNumber = remember(mobileNumber) { mobileNumber.toPersianDigits() }
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 14.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    "کد برای این شماره ارسال شد",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    displayedNumber,
                    style = MaterialTheme.typography.titleMedium.copy(
                        textDirection = TextDirection.Ltr,
                        fontFeatureSettings = "tnum",
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Start,
                )
            }
            AriaButton(
                text = "ویرایش",
                onClick = onEditNumber,
                enabled = enabled,
                style = AriaButtonStyle.Text,
                size = AriaButtonSize.Medium,
            )
        }
    }
}

@Composable
private fun OtpMessage(errorMessage: String?, infoMessage: String?) {
    val message = errorMessage ?: infoMessage ?: "کد را از پیامک بردار یا اینجا جای‌گذاری کن."
    Text(
        message,
        modifier = Modifier.fillMaxWidth(),
        style = MaterialTheme.typography.bodySmall,
        color = when {
            errorMessage != null -> MaterialTheme.colorScheme.error
            infoMessage != null -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        },
    )
}

@Composable
private fun OtpResendSection(
    secondsLeft: StateFlow<Int>,
    isLoading: Boolean,
    isResending: Boolean,
    onResend: () -> Unit,
) {
    val seconds by secondsLeft.collectAsStateWithLifecycle()
    OtpResendContent(seconds, isLoading, isResending, onResend)
}

@Composable
private fun OtpResendContent(
    secondsLeft: Int,
    isLoading: Boolean,
    isResending: Boolean,
    onResend: () -> Unit,
) {
    val timerText = remember(secondsLeft) {
        val safeSeconds = secondsLeft.coerceAtLeast(0)
        "${safeSeconds / 60}:${(safeSeconds % 60).toString().padStart(2, '0')}".toPersianDigits()
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            "کد را دریافت نکردی؟",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (secondsLeft > 0) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                shape = MaterialTheme.shapes.small,
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        timerText,
                        style = MaterialTheme.typography.titleMedium.copy(
                            textDirection = TextDirection.Ltr,
                            fontFeatureSettings = "tnum",
                        ),
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        "تا امکان ارسال دوباره",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        } else {
            AriaButton(
                text = if (isResending) "در حال ارسال" else "ارسال دوبارهٔ کد",
                onClick = onResend,
                modifier = Modifier.fillMaxWidth(),
                style = AriaButtonStyle.Secondary,
                size = AriaButtonSize.Medium,
                enabled = !isLoading,
                loading = isResending,
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
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
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
                "کد یک‌بارمصرف را با هیچ‌کس به‌اشتراک نگذار.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(name = "Compact", showBackground = true, locale = "fa", widthDp = 390, heightDp = 950)
@Composable
private fun OtpCompactPreview() = OtpPreview(WindowWidthClass.COMPACT)

@Preview(name = "Medium", showBackground = true, locale = "fa", widthDp = 700, heightDp = 950)
@Composable
private fun OtpMediumPreview() = OtpPreview(WindowWidthClass.MEDIUM)

@Preview(name = "Expanded", showBackground = true, locale = "fa", widthDp = 1280, heightDp = 800)
@Composable
private fun OtpExpandedPreview() = OtpPreview(WindowWidthClass.EXPANDED)

@Preview(name = "Error", showBackground = true, locale = "fa", widthDp = 390, heightDp = 950)
@Composable
private fun OtpErrorPreview() = OtpPreview(
    WindowWidthClass.COMPACT,
    errorMessage = "کد تأیید نادرست است؛ دوباره بررسی کن",
    secondsLeft = 0,
)

@Preview(
    name = "Dark", showBackground = true, locale = "fa", widthDp = 390, heightDp = 950,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun OtpDarkPreview() = OtpPreview(WindowWidthClass.COMPACT)

@Preview(
    name = "Small width / large font", showBackground = true, locale = "fa",
    widthDp = 320, heightDp = 1000, fontScale = 1.5f
)
@Composable
private fun OtpLargeFontPreview() = OtpPreview(WindowWidthClass.COMPACT)

@Composable
private fun OtpPreview(
    widthClass: WindowWidthClass,
    errorMessage: String? = null,
    secondsLeft: Int = 24,
) {
    val uiState = OtpUiState(
        mobileNumber = "09123456789",
        otpCode = if (errorMessage == null) "123" else "111111",
        isOtpInputEnabled = true,
        isVerifying = false,
        isResending = false,
        canVerify = errorMessage != null,
        isCodeError = errorMessage != null,
        errorMessage = errorMessage,
        infoMessage = null,
    )
    AriaTheme {
        OtpScreen(
            state = uiState,
            onCodeChange = {},
            onCodeComplete = {},
            onEditNumber = {},
            widthClass = widthClass,
            resendContent = {
                OtpResendContent(
                    secondsLeft = secondsLeft,
                    isLoading = false,
                    isResending = false,
                    onResend = {},
                )
            },
        )
    }
}
