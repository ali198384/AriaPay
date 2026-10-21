package ir.neobank.ariapay.feature.applock

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.neobank.ariapay.core.designsystem.adaptive.WindowWidthClass
import ir.neobank.ariapay.core.designsystem.adaptive.rememberWindowWidthClass
import ir.neobank.ariapay.core.designsystem.component.AriaButton
import ir.neobank.ariapay.core.designsystem.component.toPersianDigits
import ir.neobank.ariapay.core.designsystem.theme.AriaTheme
import ir.neobank.ariapay.core.security.SecureWindow
import ir.neobank.ariapay.feature.applock.contract.APP_PIN_LENGTH
import ir.neobank.ariapay.feature.applock.contract.AppPinEffect
import ir.neobank.ariapay.feature.applock.contract.AppPinError
import ir.neobank.ariapay.feature.applock.contract.AppPinEvent
import ir.neobank.ariapay.feature.applock.contract.AppPinMode
import ir.neobank.ariapay.feature.applock.contract.AppPinUiState

@Composable
fun AppPinRoute(
    onPinAccepted: () -> Unit,
    viewModel: AppPinViewModel = hiltViewModel(),
) {
    SecureWindowEffect()

    val context = LocalContext.current
    val activity = context.findFragmentActivity()
    var canAuthenticateBiometrically by remember(activity) {
        mutableStateOf(activity.canAuthenticateStrong())
    }
    val biometricPrompt = remember(activity, viewModel) {
        activity?.let { host ->
            BiometricPrompt(
                host,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(
                        result: BiometricPrompt.AuthenticationResult,
                    ) {
                        viewModel.onEvent(AppPinEvent.BiometricAuthenticated)
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        if (errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON &&
                            errorCode != BiometricPrompt.ERROR_USER_CANCELED &&
                            errorCode != BiometricPrompt.ERROR_CANCELED
                        ) {
                            viewModel.onEvent(AppPinEvent.BiometricPromptFailed)
                        }
                    }
                },
            )
        }
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, viewModel, activity) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    viewModel.onEvent(AppPinEvent.ScreenStarted)
                    canAuthenticateBiometrically = activity.canAuthenticateStrong()
                }
                Lifecycle.Event.ON_STOP -> viewModel.onEvent(AppPinEvent.ScreenStopped)
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(viewModel, onPinAccepted) {
        viewModel.effect.collect { effect ->
            if (effect == AppPinEffect.PinAccepted) onPinAccepted()
        }
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    AppPinScreen(
        state = state,
        onDigitPressed = { digit -> viewModel.onEvent(AppPinEvent.DigitPressed(digit)) },
        onDeletePressed = { viewModel.onEvent(AppPinEvent.DeletePressed) },
        onRetry = { viewModel.onEvent(AppPinEvent.RetryInitialization) },
        isBiometricAvailable = canAuthenticateBiometrically && biometricPrompt != null,
        onBiometricPressed = {
            biometricPrompt?.authenticate(createBiometricPromptInfo())
        },
        widthClass = rememberWindowWidthClass(),
    )
}

/** UI for creating a first PIN or unlocking with an existing PIN. */
@Composable
fun AppPinScreen(
    state: AppPinUiState,
    onDigitPressed: (Char) -> Unit,
    onDeletePressed: () -> Unit,
    onRetry: () -> Unit,
    isBiometricAvailable: Boolean = false,
    onBiometricPressed: () -> Unit = {},
    widthClass: WindowWidthClass = WindowWidthClass.COMPACT,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        val contentWidth = when (widthClass) {
            WindowWidthClass.COMPACT -> 440.dp
            WindowWidthClass.MEDIUM -> 480.dp
            WindowWidthClass.EXPANDED -> 520.dp
        }
        val isShortScreen = maxHeight < 680.dp
        val verticalGap = if (isShortScreen) 14.dp else 22.dp

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .widthIn(max = contentWidth)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = 24.dp,
                    vertical = if (isShortScreen) 16.dp else 28.dp,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(verticalGap),
        ) {
            when (state.mode) {
                AppPinMode.INITIALIZING -> InitializingContent()
                AppPinMode.UNAVAILABLE -> UnavailableContent(
                    error = state.error,
                    onRetry = onRetry,
                )

                AppPinMode.CREATE,
                AppPinMode.CONFIRM,
                AppPinMode.UNLOCK -> PinEntryContent(
                    state = state,
                    onDigitPressed = onDigitPressed,
                    onDeletePressed = onDeletePressed,
                    isBiometricAvailable = isBiometricAvailable,
                    onBiometricPressed = onBiometricPressed,
                    isShortScreen = isShortScreen,
                )
            }
        }
    }
}

@Composable
private fun PinEntryContent(
    state: AppPinUiState,
    onDigitPressed: (Char) -> Unit,
    onDeletePressed: () -> Unit,
    isBiometricAvailable: Boolean,
    onBiometricPressed: () -> Unit,
    isShortScreen: Boolean,
) {
    PinHeader(mode = state.mode)

    PinDots(
        enteredDigitCount = state.enteredDigitCount,
        isError = state.error != null,
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(28.dp),
        contentAlignment = Alignment.Center,
    ) {
        val errorMessage = state.error?.toPersianMessage()
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        } else if (state.isLoading) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                )
                Text(
                    text = "در حال بررسی رمز...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }

    PinKeypad(
        enabled = !state.isLoading,
        canDelete = state.enteredDigitCount > 0,
        showBiometricKey = state.mode == AppPinMode.UNLOCK && isBiometricAvailable,
        onBiometricPressed = onBiometricPressed,
        onDigitPressed = onDigitPressed,
        onDeletePressed = onDeletePressed,
    )

    if (!isShortScreen) Spacer(Modifier.height(4.dp))
    SecurityNotice()
}

@Composable
private fun PinHeader(mode: AppPinMode) {
    val title: String
    val subtitle: String
    when (mode) {
        AppPinMode.CREATE -> {
            title = "ساخت رمز ورود"
            subtitle = "برای محافظت از حساب، یک رمز ۴ رقمی انتخاب کنید."
        }

        AppPinMode.CONFIRM -> {
            title = "تأیید رمز ورود"
            subtitle = "همان رمز ۴ رقمی را دوباره وارد کنید."
        }

        else -> {
            title = "ورود امن به آریاپی"
            subtitle = "برای ادامه، رمز ۴ رقمی خود را وارد کنید."
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Surface(
            modifier = Modifier.size(68.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(30.dp),
                )
            }
        }
        Text(
            text = "آریاپی",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge,
        )
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )
        Text(
            text = subtitle,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun PinDots(
    enteredDigitCount: Int,
    isError: Boolean,
) {
    val count = enteredDigitCount.coerceIn(0, APP_PIN_LENGTH)
    val dotColor = if (isError) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.primary
    }

    Row(
        modifier = Modifier.clearAndSetSemantics {
            contentDescription =
                "${count.toString().toPersianDigits()} رقم از ${APP_PIN_LENGTH.toString().toPersianDigits()} رقم وارد شده"
        },
        horizontalArrangement = Arrangement.spacedBy(18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(APP_PIN_LENGTH) { index ->
            val isFilled = index < count
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(if (isFilled) dotColor else Color.Transparent)
                    .border(
                        border = BorderStroke(
                            width = 1.5.dp,
                            color = if (isFilled) dotColor else MaterialTheme.colorScheme.outline,
                        ),
                        shape = CircleShape,
                    ),
            )
        }
    }
}

@Composable
private fun PinKeypad(
    enabled: Boolean,
    canDelete: Boolean,
    showBiometricKey: Boolean,
    onBiometricPressed: () -> Unit,
    onDigitPressed: (Char) -> Unit,
    onDeletePressed: () -> Unit,
) {
    androidx.compose.runtime.CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Ltr,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 330.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            pinKeypadRows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 8.dp,
                        alignment = Alignment.CenterHorizontally,
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    row.forEach { key ->
                        if (key == null) {
                            if (showBiometricKey) {
                                BiometricKeyButton(
                                    enabled = enabled,
                                    onClick = onBiometricPressed,
                                )
                            } else {
                                Spacer(Modifier.size(width = 76.dp, height = 64.dp))
                            }
                        } else {
                            PinKeyButton(
                                key = key,
                                enabled = enabled && (key !is PinKey.Delete || canDelete),
                                onDigitPressed = onDigitPressed,
                                onDeletePressed = onDeletePressed,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BiometricKeyButton(
    enabled: Boolean,
    onClick: () -> Unit,
) {
    FilledTonalButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .size(width = 76.dp, height = 64.dp)
            .semantics {
                contentDescription = "ورود با اثر انگشت یا تشخیص چهره"
                role = Role.Button
            },
        shape = RoundedCornerShape(22.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Icon(
            imageVector = Icons.Filled.Fingerprint,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )
    }
}

@Composable
private fun PinKeyButton(
    key: PinKey,
    enabled: Boolean,
    onDigitPressed: (Char) -> Unit,
    onDeletePressed: () -> Unit,
) {
    FilledTonalButton(
        onClick = {
            when (key) {
                is PinKey.Digit -> onDigitPressed(key.value)
                PinKey.Delete -> onDeletePressed()
            }
        },
        enabled = enabled,
        modifier = Modifier
            .size(width = 76.dp, height = 64.dp)
            .then(
                if (key == PinKey.Delete) {
                    Modifier.semantics {
                        contentDescription = "حذف آخرین رقم"
                        role = Role.Button
                    }
                } else {
                    Modifier
                },
            ),
        shape = RoundedCornerShape(22.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        when (key) {
            is PinKey.Digit -> Text(
                text = key.value.toString().toPersianDigits(),
                style = MaterialTheme.typography.titleLarge,
            )

            PinKey.Delete -> Icon(
                imageVector = Icons.Filled.Backspace,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Composable
private fun SecurityNotice() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = "ورود با صفحه‌کلید اختصاصی انجام می‌شود و اسکرین‌شات این صفحه غیرفعال است.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun InitializingContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 120.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        CircularProgressIndicator()
        Text("در حال آماده‌سازی ورود امن...", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun UnavailableContent(
    error: AppPinError?,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.Lock,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp),
        )
        Text(
            text = error?.toPersianMessage() ?: "بارگذاری رمز ورود ممکن نشد.",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        AriaButton(
            text = "تلاش دوباره",
            onClick = onRetry,
        )
    }
}

private fun AppPinError.toPersianMessage(): String = when (this) {
    AppPinError.INCORRECT -> "رمز واردشده درست نیست. دوباره تلاش کنید."
    AppPinError.MISMATCH -> "دو رمز یکسان نبودند؛ رمز تازه‌ای انتخاب کنید."
    AppPinError.SESSION_UNAVAILABLE -> "نشست قابل بازشدن نیست. دوباره تلاش کنید."
    AppPinError.BIOMETRIC_FAILED -> "تأیید بیومتریک انجام نشد؛ با رمز برنامه وارد شوید."
    AppPinError.STORAGE -> "دسترسی امن به رمز ممکن نشد. دوباره تلاش کنید."
}

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

private fun Context.findFragmentActivity(): FragmentActivity? = when (this) {
    is FragmentActivity -> this
    is ContextWrapper -> baseContext.findFragmentActivity()
    else -> null
}

private fun FragmentActivity?.canAuthenticateStrong(): Boolean =
    this?.let { activity ->
        BiometricManager.from(activity)
            .canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) ==
            BiometricManager.BIOMETRIC_SUCCESS
    } == true

private fun createBiometricPromptInfo(): BiometricPrompt.PromptInfo =
    BiometricPrompt.PromptInfo.Builder()
        .setTitle("ورود امن به آریاپی")
        .setSubtitle("اثر انگشت یا تشخیص چهره را تأیید کنید")
        .setNegativeButtonText("استفاده از رمز برنامه")
        .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
        .build()

@Composable
private fun SecureWindowEffect() {
    val context = LocalContext.current
    val window = context.findActivity()?.window

    DisposableEffect(window) {
        if (window == null) {
            onDispose { }
        } else {
            val wasSecure = SecureWindow.isEnabled(window)
            SecureWindow.setEnabled(window, enabled = true)
            onDispose {
                SecureWindow.setEnabled(window, enabled = wasSecure)
            }
        }
    }
}

private sealed interface PinKey {
    data class Digit(val value: Char) : PinKey
    data object Delete : PinKey
}

private val pinKeypadRows: List<List<PinKey?>> = listOf(
    listOf(PinKey.Digit('1'), PinKey.Digit('2'), PinKey.Digit('3')),
    listOf(PinKey.Digit('4'), PinKey.Digit('5'), PinKey.Digit('6')),
    listOf(PinKey.Digit('7'), PinKey.Digit('8'), PinKey.Digit('9')),
    listOf(null, PinKey.Digit('0'), PinKey.Delete),
)

@Preview(showBackground = true, locale = "fa")
@Preview(showBackground = true, locale = "fa", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AppPinScreenPreview() {
    AriaTheme {
        Surface {
            AppPinScreen(
                state = AppPinUiState(
                    mode = AppPinMode.UNLOCK,
                    enteredDigitCount = 2,
                ),
                onDigitPressed = {},
                onDeletePressed = {},
                onRetry = {},
            )
        }
    }
}
