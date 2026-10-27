package ir.neobank.ariapay.feature.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.SettingsBrightness
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import ir.neobank.ariapay.core.common.settings.AppThemeMode
import ir.neobank.ariapay.core.designsystem.adaptive.WindowWidthClass
import ir.neobank.ariapay.core.designsystem.adaptive.rememberWindowWidthClass
import ir.neobank.ariapay.core.designsystem.theme.AriaTheme
import ir.neobank.ariapay.feature.profile.contract.ProfileActionError
import ir.neobank.ariapay.feature.profile.contract.ProfileEffect
import ir.neobank.ariapay.feature.profile.contract.ProfileEvent
import ir.neobank.ariapay.feature.profile.contract.ProfileUiError
import ir.neobank.ariapay.feature.profile.contract.ProfileUiState
import ir.neobank.ariapay.feature.profile.domain.model.UserProfile
import java.io.File
import java.util.Locale

@Composable
fun ProfileRoute(
    appVersionName: String,
    onBack: () -> Unit,
    onNavigateToAuthentication: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        uri?.let { viewModel.onEvent(ProfileEvent.ProfilePhotoSelected(it.toString())) }
    }

    LaunchedEffect(viewModel, onNavigateToAuthentication) {
        viewModel.effect.collect { effect ->
            when (effect) {
                ProfileEffect.NavigateToAuthentication -> onNavigateToAuthentication()
            }
        }
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ProfileScreen(
        state = state,
        appVersionName = appVersionName,
        onBack = onBack,
        onRetry = { viewModel.onEvent(ProfileEvent.RetryClicked) },
        onEvent = viewModel::onEvent,
        onPickPhoto = {
            photoPicker.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
            )
        },
        widthClass = rememberWindowWidthClass(),
    )
}

@Composable
fun ProfileScreen(
    state: ProfileUiState,
    appVersionName: String,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onEvent: (ProfileEvent) -> Unit,
    onPickPhoto: () -> Unit,
    widthClass: WindowWidthClass = WindowWidthClass.COMPACT,
) {
    val horizontalPadding = when (widthClass) {
        WindowWidthClass.COMPACT -> 20.dp
        WindowWidthClass.MEDIUM -> 32.dp
        WindowWidthClass.EXPANDED -> 48.dp
    }
    val maxContentWidth = when (widthClass) {
        WindowWidthClass.COMPACT -> 600.dp
        WindowWidthClass.MEDIUM -> 900.dp
        WindowWidthClass.EXPANDED -> 1_120.dp
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        ProfileTopBar(onBack = onBack)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .widthIn(max = maxContentWidth)
                .align(Alignment.CenterHorizontally)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = horizontalPadding, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            when {
                state.profile != null -> ProfileContent(
                    state = state,
                    widthClass = widthClass,
                    onRetry = onRetry,
                    onEvent = onEvent,
                    onPickPhoto = onPickPhoto,
                )
                state.isLoading -> ProfileLoadingState()
                state.error != null -> ProfileErrorState(state.error, onRetry)
                state.isEmpty -> ProfileEmptyState()
            }

            ProfilePreferencesCard(state = state, onEvent = onEvent)
            AppVersionCard(versionName = appVersionName)
        }
    }

    if (state.isNameDialogVisible) {
        NameEditorDialog(state = state, onEvent = onEvent)
    }
    if (state.isLogoutDialogVisible) {
        LogoutConfirmationDialog(state = state, onEvent = onEvent)
    }
}

@Composable
private fun ProfileTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "بازگشت")
        }
        Text(
            text = "پروفایل",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.size(48.dp))
    }
}

@Composable
private fun ProfileContent(
    state: ProfileUiState,
    widthClass: WindowWidthClass,
    onRetry: () -> Unit,
    onEvent: (ProfileEvent) -> Unit,
    onPickPhoto: () -> Unit,
) {
    val profile = state.profile ?: return
    if (widthClass == WindowWidthClass.EXPANDED) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Column(Modifier.weight(0.92f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ProfileHeroCard(profile, state.isUpdatingPhoto, onPickPhoto)
                state.photoError?.let { ActionErrorCard(it) }
                if (state.isLoading) LinearProgressIndicator(Modifier.fillMaxWidth())
            }
            Column(Modifier.weight(1.08f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                state.error?.let { ProfileInlineError(it, onRetry) }
                ProfileInformationCard(profile)
            }
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ProfileHeroCard(profile, state.isUpdatingPhoto, onPickPhoto)
            state.photoError?.let { ActionErrorCard(it) }
            if (state.isLoading) LinearProgressIndicator(Modifier.fillMaxWidth())
            state.error?.let { ProfileInlineError(it, onRetry) }
            ProfileInformationCard(profile)
        }
    }
    OutlinedButton(
        onClick = { onEvent(ProfileEvent.EditNameClicked) },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Icon(Icons.Outlined.Person, contentDescription = null)
        Text("ویرایش نام")
    }
}

@Composable
private fun ProfileHeroCard(
    profile: UserProfile,
    isUpdatingPhoto: Boolean,
    onPickPhoto: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = colors.primary),
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                ProfileAvatar(
                    profile = profile,
                    isUpdating = isUpdatingPhoto,
                    onClick = onPickPhoto,
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Text(
                        text = "حساب کاربری آریاپی",
                        style = MaterialTheme.typography.labelLarge,
                        color = colors.onPrimary.copy(alpha = 0.78f),
                    )
                    Text(
                        text = profile.fullName.nonBlankOr("نام ثبت نشده"),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.onPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "برای تغییر تصویر، روی عکس بزنید",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onPrimary.copy(alpha = 0.78f),
                    )
                }
            }
            HorizontalDivider(color = colors.onPrimary.copy(alpha = 0.18f))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(Icons.Outlined.VerifiedUser, contentDescription = null, tint = colors.onPrimary)
                Text(
                    text = "سطح احراز هویت",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onPrimary.copy(alpha = 0.82f),
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = formatKycLevel(profile.kycLevel),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.onPrimary,
                )
            }
        }
    }
}

@Composable
private fun ProfileAvatar(profile: UserProfile, isUpdating: Boolean, onClick: () -> Unit) {
    val photoFile = profile.profilePhotoPath
        ?.takeIf(String::isNotBlank)
        ?.let(::File)
    val onPrimary = MaterialTheme.colorScheme.onPrimary

    Box(
        modifier = Modifier
            .size(92.dp)
            .clip(CircleShape)
            .background(onPrimary.copy(alpha = 0.14f))
            .clickable(
                enabled = !isUpdating,
                role = Role.Button,
                onClickLabel = "تغییر تصویر پروفایل",
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = profile.fullName.toInitials(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = onPrimary,
        )
        if (photoFile != null) {
            AsyncImage(
                model = photoFile,
                contentDescription = null,
                modifier = Modifier.fillMaxSize().clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
        }
        Surface(
            modifier = Modifier.align(Alignment.BottomEnd).size(30.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (isUpdating) {
                    CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Outlined.CameraAlt, contentDescription = null, modifier = Modifier.size(17.dp))
                }
            }
        }
    }
}

@Composable
private fun ProfileInformationCard(profile: UserProfile) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("اطلاعات حساب", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            ProfileInfoRow(
                icon = Icons.Outlined.PhoneAndroid,
                label = "شمارهٔ همراه",
                value = profile.mobileNumberMasked.nonBlankOr("ثبت نشده"),
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f))
            ProfileInfoRow(
                icon = Icons.Outlined.VerifiedUser,
                label = "وضعیت احراز هویت",
                value = formatKycStatus(profile.kycStatus),
            )
            ProfileInfoRow(
                icon = Icons.Outlined.AccountBalance,
                label = "وضعیت حساب",
                value = formatAccountStatus(profile.accountStatus),
            )
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Surface(
            modifier = Modifier.size(42.dp),
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ) {
            Box(contentAlignment = Alignment.Center) { Icon(icon, contentDescription = null) }
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun ProfilePreferencesCard(state: ProfileUiState, onEvent: (ProfileEvent) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text("تنظیمات نمایش", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text("پوستهٔ برنامه", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ThemeChoice(
                    selected = state.themeMode == AppThemeMode.SYSTEM,
                    enabled = !state.isSavingTheme,
                    label = "سیستم",
                    icon = Icons.Outlined.SettingsBrightness,
                    onClick = { onEvent(ProfileEvent.ThemeModeSelected(AppThemeMode.SYSTEM)) },
                )
                ThemeChoice(
                    selected = state.themeMode == AppThemeMode.LIGHT,
                    enabled = !state.isSavingTheme,
                    label = "روشن",
                    icon = Icons.Outlined.LightMode,
                    onClick = { onEvent(ProfileEvent.ThemeModeSelected(AppThemeMode.LIGHT)) },
                )
                ThemeChoice(
                    selected = state.themeMode == AppThemeMode.DARK,
                    enabled = !state.isSavingTheme,
                    label = "تیره",
                    icon = Icons.Outlined.DarkMode,
                    onClick = { onEvent(ProfileEvent.ThemeModeSelected(AppThemeMode.DARK)) },
                )
            }
            if (state.isSavingTheme) LinearProgressIndicator(Modifier.fillMaxWidth())
            state.themeError?.let { ActionErrorText(it) }
            if (state.isAuthenticated) {
                HorizontalDivider()
                OutlinedButton(
                    onClick = { onEvent(ProfileEvent.LogoutClicked) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoggingOut,
                ) {
                    Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = null)
                    Text("خروج از حساب")
                }
            }
        }
    }
}

@Composable
private fun ThemeChoice(
    selected: Boolean,
    enabled: Boolean,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        enabled = enabled,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp)) },
    )
}

@Composable
private fun ProfileInlineError(error: ProfileUiError, onRetry: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        shape = RoundedCornerShape(18.dp),
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 8.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(Icons.Outlined.ErrorOutline, contentDescription = null)
            Text(profileErrorMessage(error), modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
            if (error == ProfileUiError.PROFILE_LOAD_FAILED) TextButton(onClick = onRetry) { Text("تلاش دوباره") }
        }
    }
}

@Composable
private fun ProfileErrorState(error: ProfileUiError, onRetry: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(Icons.Outlined.ErrorOutline, contentDescription = null, modifier = Modifier.size(40.dp))
            Text(profileErrorMessage(error), style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
            if (error == ProfileUiError.PROFILE_LOAD_FAILED) TextButton(onClick = onRetry) { Text("تلاش دوباره") }
        }
    }
}

@Composable
private fun ProfileEmptyState() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(24.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(Icons.Outlined.Person, contentDescription = null, modifier = Modifier.size(38.dp), tint = MaterialTheme.colorScheme.primary)
            Text("اطلاعات پروفایل هنوز آماده نیست", style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
            Text(
                "پس از دریافت اطلاعات حساب، جزئیات این بخش نمایش داده می‌شود.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ProfileLoadingState() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        LinearProgressIndicator(Modifier.fillMaxWidth())
        Text("در حال دریافت اطلاعات پروفایل…", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun NameEditorDialog(state: ProfileUiState, onEvent: (ProfileEvent) -> Unit) {
    AlertDialog(
        onDismissRequest = { onEvent(ProfileEvent.DismissNameDialog) },
        title = { Text("ویرایش نام") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = state.nameDraft,
                    onValueChange = { value ->
                        onEvent(ProfileEvent.NameDraftChanged(value.take(MAX_NAME_LENGTH)))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("نام و نام خانوادگی") },
                    singleLine = true,
                    enabled = !state.isSavingName,
                    isError = state.nameError != null,
                )
                state.nameError?.let { ActionErrorText(it) }
            }
        },
        confirmButton = {
            Button(
                onClick = { onEvent(ProfileEvent.SaveNameClicked) },
                enabled = !state.isSavingName,
            ) {
                if (state.isSavingName) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                else Text("ذخیره")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onEvent(ProfileEvent.DismissNameDialog) },
                enabled = !state.isSavingName,
            ) { Text("انصراف") }
        },
    )
}

@Composable
private fun LogoutConfirmationDialog(state: ProfileUiState, onEvent: (ProfileEvent) -> Unit) {
    AlertDialog(
        onDismissRequest = { onEvent(ProfileEvent.DismissLogoutDialog) },
        title = { Text("خروج از حساب") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("از حساب کاربری خارج می‌شوید و اطلاعات نشست ذخیره‌شده روی این دستگاه پاک می‌شود. ادامه می‌دهید؟")
                state.logoutError?.let { ActionErrorText(it) }
            }
        },
        confirmButton = {
            Button(
                onClick = { onEvent(ProfileEvent.ConfirmLogoutClicked) },
                enabled = !state.isLoggingOut,
            ) {
                if (state.isLoggingOut) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                else Text("خروج")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onEvent(ProfileEvent.DismissLogoutDialog) },
                enabled = !state.isLoggingOut,
            ) { Text("انصراف") }
        },
    )
}

@Composable
private fun ActionErrorCard(error: ProfileActionError) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(Icons.Outlined.ErrorOutline, contentDescription = null)
            Text(actionErrorMessage(error), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun ActionErrorText(error: ProfileActionError) {
    Text(
        text = actionErrorMessage(error),
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodySmall,
    )
}

@Composable
private fun AppVersionCard(versionName: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.62f),
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        shape = RoundedCornerShape(18.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(Icons.Outlined.Info, contentDescription = null)
            Text("نسخهٔ برنامه", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
            Text(versionName.nonBlankOr("—"), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
        }
    }
}

private fun formatKycLevel(value: String): String = when (value.normalizedStatus()) {
    "BASIC", "LEVEL_0", "L0", "0" -> "سطح پایه"
    "LEVEL_1", "L1", "1" -> "سطح ۱"
    "LEVEL_2", "L2", "2" -> "سطح ۲"
    "LEVEL_3", "L3", "3" -> "سطح ۳"
    else -> value.nonBlankOr("نامشخص")
}

private fun formatKycStatus(value: String): String = when (value.normalizedStatus()) {
    "VERIFIED", "APPROVED", "COMPLETED", "SUCCESS" -> "تأیید شده"
    "PENDING", "IN_PROGRESS", "PROCESSING" -> "در حال بررسی"
    "REJECTED", "FAILED", "NEEDS_CORRECTION" -> "نیازمند اصلاح"
    "NOT_STARTED", "UNVERIFIED", "INCOMPLETE" -> "تکمیل نشده"
    else -> value.nonBlankOr("نامشخص")
}

private fun formatAccountStatus(value: String): String = when (value.normalizedStatus()) {
    "ACTIVE", "ENABLED", "OPEN" -> "فعال"
    "PENDING", "IN_PROGRESS" -> "در حال بررسی"
    "BLOCKED", "SUSPENDED", "DISABLED" -> "مسدود"
    "INACTIVE", "CLOSED" -> "غیرفعال"
    else -> value.nonBlankOr("نامشخص")
}

private fun profileErrorMessage(error: ProfileUiError): String = when (error) {
    ProfileUiError.AUTHENTICATION_REQUIRED -> "برای مشاهدهٔ پروفایل، ابتدا وارد حساب شوید."
    ProfileUiError.PROFILE_LOAD_FAILED -> "بارگذاری اطلاعات پروفایل انجام نشد."
}

private fun actionErrorMessage(error: ProfileActionError): String = when (error) {
    ProfileActionError.NAME_REQUIRED -> "نام نمی‌تواند خالی باشد."
    ProfileActionError.NAME_SAVE_FAILED -> "ذخیرهٔ نام انجام نشد. دوباره تلاش کنید."
    ProfileActionError.PHOTO_SAVE_FAILED -> "ذخیرهٔ تصویر انجام نشد. تصویر دیگری انتخاب کنید."
    ProfileActionError.THEME_SAVE_FAILED -> "ذخیرهٔ تنظیم پوسته انجام نشد."
    ProfileActionError.LOGOUT_FAILED -> "پاک‌سازی اطلاعات کامل نشد. نشست فعال است؛ دوباره تلاش کنید."
}

private fun String?.nonBlankOr(fallback: String): String = this?.trim()?.takeIf(String::isNotEmpty) ?: fallback

private fun String?.toInitials(): String {
    val words = this.orEmpty().trim().split(Regex("\\s+")).filter(String::isNotEmpty)
    return words.take(2).joinToString(separator = "") { it.take(1) }.ifEmpty { "آ" }
}

private fun String.normalizedStatus(): String =
    trim().uppercase(Locale.ROOT).replace('-', '_').replace(' ', '_')

private const val MAX_NAME_LENGTH = 60

@Preview(showBackground = true, name = "Profile - Compact", widthDp = 393, heightDp = 852)
@Composable
private fun ProfileScreenCompactPreview() {
    AriaTheme {
        ProfileScreen(
            state = previewProfileState,
            appVersionName = "1.0",
            onBack = {},
            onRetry = {},
            onEvent = {},
            onPickPhoto = {},
        )
    }
}

@Preview(showBackground = true, name = "Profile - Expanded", widthDp = 1000, heightDp = 800)
@Composable
private fun ProfileScreenExpandedPreview() {
    AriaTheme {
        ProfileScreen(
            state = previewProfileState,
            appVersionName = "1.0",
            onBack = {},
            onRetry = {},
            onEvent = {},
            onPickPhoto = {},
            widthClass = WindowWidthClass.EXPANDED,
        )
    }
}

private val previewProfileState = ProfileUiState(
    isLoading = false,
    isAuthenticated = true,
    profile = UserProfile(
        userId = "demo-user-001",
        fullName = "نگار محمدی",
        mobileNumberMasked = "۰۹۱۲ *** ** ۶۷",
        nationalCodeMasked = "۰۰۱*****۸۹",
        email = null,
        profilePhotoPath = null,
        kycLevel = "LEVEL_2",
        kycStatus = "VERIFIED",
        accountStatus = "ACTIVE",
        serverUpdatedAtEpochMillis = null,
    ),
)
