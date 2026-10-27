package ir.neobank.ariapay.feature.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import ir.neobank.ariapay.core.designsystem.adaptive.WindowWidthClass
import ir.neobank.ariapay.core.designsystem.adaptive.rememberWindowWidthClass
import ir.neobank.ariapay.core.designsystem.theme.AriaTheme
import ir.neobank.ariapay.feature.profile.contract.ProfileEffect
import ir.neobank.ariapay.feature.profile.contract.ProfileEvent
import ir.neobank.ariapay.feature.profile.contract.ProfileUiError
import ir.neobank.ariapay.feature.profile.contract.ProfileUiState
import ir.neobank.ariapay.feature.profile.domain.UserProfile
import java.io.File
import java.util.Locale

@Composable
fun ProfileRoute(
    appVersionName: String,
    onBack: () -> Unit,
    onNavigateToAuthentication: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
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
        widthClass = rememberWindowWidthClass()
    )
}

@Composable
fun ProfileScreen(
    state: ProfileUiState,
    appVersionName: String,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    widthClass: WindowWidthClass = WindowWidthClass.COMPACT
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
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = maxContentWidth)
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = horizontalPadding, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                when {
                    state.profile != null -> ProfileContent(
                        profile = state.profile,
                        isLoading = state.isLoading,
                        error = state.error,
                        widthClass = widthClass,
                        onRetry = onRetry,
                    )

                    state.isLoading -> ProfileLoadingSkeleton(widthClass = widthClass)
                    state.error != null -> ProfileErrorState(
                        error = state.error,
                        onRetry = onRetry,
                    )

                    state.isEmpty -> ProfileEmptyState()
                    else -> ProfileLoadingSkeleton(widthClass = widthClass)
                }
                AppVersionCard(versionName = appVersionName)
            }
        }
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
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "بازگشت",
            )
        }
        Text(
            text = "پروفایل",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.size(48.dp))
    }
}

@Composable
private fun ProfileContent(
    profile: UserProfile,
    isLoading: Boolean,
    error: ProfileUiError?,
    widthClass: WindowWidthClass,
    onRetry: () -> Unit,
) {
    if (widthClass != WindowWidthClass.EXPANDED) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ProfileHeroCard(profile = profile)
            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            error?.let { ProfileInlineError(error = it, onRetry = onRetry) }
            ProfileInformationCard(profile = profile)
        }
    } else {
        Row(
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                modifier = Modifier.weight(0.92f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                ProfileHeroCard(profile = profile)
                if (isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }
            Column(
                modifier = Modifier.weight(1.08f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                error?.let { ProfileInlineError(error = it, onRetry = onRetry) }
                ProfileInformationCard(profile = profile)
            }
        }
    }
}

@Composable
private fun ProfileHeroCard(profile: UserProfile) {
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
                ProfileAvatar(profile = profile)
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
                        text = "پروفایل شخصی",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onPrimary.copy(alpha = 0.78f),
                    )
                }
            }
            HorizontalDivider(color = colors.onPrimary.copy(alpha = 0.18f))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.VerifiedUser,
                    contentDescription = null,
                    tint = colors.onPrimary,
                )
                Text(
                    text = "سطح احراز هویت",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onPrimary.copy(alpha = 0.82f),
                )
                Spacer(modifier = Modifier.weight(1f))
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
private fun ProfileAvatar(profile: UserProfile) {
    val photoFile = profile.profilePhotoPath
        ?.takeIf(String::isNotBlank)
        ?.let(::File)
    val onPrimary = MaterialTheme.colorScheme.onPrimary

    Box(
        modifier = Modifier
            .size(92.dp)
            .clip(CircleShape)
            .background(onPrimary.copy(alpha = 0.14f))
            .border(
                width = 2.dp,
                color = onPrimary.copy(alpha = 0.42f),
                shape = CircleShape,
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
                contentDescription = "تصویر پروفایل ${profile.fullName.nonBlankOr("کاربر")}",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
        }
    }
}

@Composable
private fun ProfileInformationCard(profile: UserProfile) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "اطلاعات حساب",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
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
                tone = kycStatusTone(profile.kycStatus),
            )
            ProfileInfoRow(
                icon = Icons.Outlined.AccountBalance,
                label = "وضعیت حساب",
                value = formatAccountStatus(profile.accountStatus),
                tone = accountStatusTone(profile.accountStatus),
            )
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    tone: ProfileStatusTone = ProfileStatusTone.NEUTRAL,
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
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null)
            }
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (tone != ProfileStatusTone.NEUTRAL) ProfileStatusDot(tone = tone)
    }
}

@Composable
private fun ProfileStatusDot(tone: ProfileStatusTone) {
    val color = when (tone) {
        ProfileStatusTone.SUCCESS -> AriaTheme.extra.success
        ProfileStatusTone.WARNING -> AriaTheme.extra.warning
        ProfileStatusTone.ERROR -> MaterialTheme.colorScheme.error
        ProfileStatusTone.NEUTRAL -> MaterialTheme.colorScheme.outline
    }
    Box(
        modifier = Modifier
            .size(9.dp)
            .clip(CircleShape)
            .background(color),
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
            Icon(imageVector = Icons.Outlined.ErrorOutline, contentDescription = null)
            Text(
                text = profileErrorMessage(error),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
            )
            if (error == ProfileUiError.PROFILE_LOAD_FAILED) {
                TextButton(onClick = onRetry) { Text("تلاش دوباره") }
            }
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.onErrorContainer,
            )
            Text(
                text = profileErrorMessage(error),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center,
            )
            if (error == ProfileUiError.PROFILE_LOAD_FAILED) {
                TextButton(onClick = onRetry) { Text("تلاش دوباره") }
            }
        }
    }
}

@Composable
private fun ProfileEmptyState() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Surface(
                modifier = Modifier.size(68.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                    )
                }
            }
            Text(
                text = "اطلاعات پروفایل هنوز آماده نیست",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "پس از دریافت اطلاعات حساب، جزئیات این بخش نمایش داده می‌شود.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ProfileLoadingSkeleton(widthClass: WindowWidthClass) {
    if (widthClass != WindowWidthClass.EXPANDED) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ProfileHeroSkeleton()
            ProfileDetailsSkeleton()
        }
    } else {
        Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
            Column(modifier = Modifier.weight(0.92f)) { ProfileHeroSkeleton() }
            Column(modifier = Modifier.weight(1.08f)) { ProfileDetailsSkeleton() }
        }
    }
}

@Composable
private fun ProfileHeroSkeleton() {
    val placeholder = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier.padding(22.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(placeholder),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                SkeletonBlock(widthFraction = 0.52f, height = 12.dp, color = placeholder)
                SkeletonBlock(widthFraction = 0.82f, height = 22.dp, color = placeholder)
                SkeletonBlock(widthFraction = 0.42f, height = 12.dp, color = placeholder)
            }
        }
    }
}

@Composable
private fun ProfileDetailsSkeleton() {
    val placeholder = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            SkeletonBlock(widthFraction = 0.45f, height = 18.dp, color = placeholder)
            repeat(3) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(placeholder),
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(7.dp),
                    ) {
                        SkeletonBlock(widthFraction = 0.4f, height = 10.dp, color = placeholder)
                        SkeletonBlock(widthFraction = 0.66f, height = 15.dp, color = placeholder)
                    }
                }
            }
        }
    }
}

@Composable
private fun SkeletonBlock(widthFraction: Float, height: androidx.compose.ui.unit.Dp, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth(widthFraction)
            .height(height)
            .clip(RoundedCornerShape(50))
            .background(color),
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
            Icon(imageVector = Icons.Outlined.Info, contentDescription = null)
            Text(
                text = "نسخهٔ برنامه",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = versionName.nonBlankOr("—"),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

private enum class ProfileStatusTone { SUCCESS, WARNING, ERROR, NEUTRAL }

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

private fun kycStatusTone(value: String): ProfileStatusTone = when (value.normalizedStatus()) {
    "VERIFIED", "APPROVED", "COMPLETED", "SUCCESS" -> ProfileStatusTone.SUCCESS
    "PENDING", "IN_PROGRESS", "PROCESSING" -> ProfileStatusTone.WARNING
    "REJECTED", "FAILED", "NEEDS_CORRECTION" -> ProfileStatusTone.ERROR
    else -> ProfileStatusTone.NEUTRAL
}

private fun accountStatusTone(value: String): ProfileStatusTone = when (value.normalizedStatus()) {
    "ACTIVE", "ENABLED", "OPEN" -> ProfileStatusTone.SUCCESS
    "PENDING", "IN_PROGRESS" -> ProfileStatusTone.WARNING
    "BLOCKED", "SUSPENDED", "DISABLED", "INACTIVE", "CLOSED" -> ProfileStatusTone.ERROR
    else -> ProfileStatusTone.NEUTRAL
}

private fun profileErrorMessage(error: ProfileUiError): String = when (error) {
    ProfileUiError.AUTHENTICATION_REQUIRED -> "برای مشاهدهٔ پروفایل، ابتدا وارد حساب شوید."
    ProfileUiError.PROFILE_LOAD_FAILED -> "بارگذاری اطلاعات پروفایل انجام نشد."
}

private fun String?.nonBlankOr(fallback: String): String = this?.trim()?.takeIf(String::isNotEmpty) ?: fallback

private fun String?.toInitials(): String {
    val words = this.orEmpty().trim().split(Regex("\\s+")).filter(String::isNotEmpty)
    return words.take(2).joinToString(separator = "") { it.take(1) }.ifEmpty { "آ" }
}

private fun String.normalizedStatus(): String =
    trim().uppercase(Locale.ROOT).replace('-', '_').replace(' ', '_')

@Preview(showBackground = true, name = "Profile - Compact", widthDp = 393, heightDp = 852)
@Composable
private fun ProfileScreenCompactPreview() {
    AriaTheme {
        ProfileScreen(
            state = ProfileUiState(
                isLoading = false,
                profile = UserProfile(
                    userId = "demo-user-001",
                    fullName = "نگار محمدی",
                    mobileNumberMasked = "۶۷ *** ** ۰۹۱۲",
                    nationalCodeMasked = "۰۰۱*****۸۹",
                    email = null,
                    profilePhotoPath = null,
                    kycLevel = "LEVEL_2",
                    kycStatus = "VERIFIED",
                    accountStatus = "ACTIVE",
                    serverUpdatedAtEpochMillis = null,
                ),
            ),
            appVersionName = "1.0",
            onBack = {},
            onRetry = {},
        )
    }
}

@Preview(showBackground = true, name = "Profile - Expanded", widthDp = 1000, heightDp = 800)
@Composable
private fun ProfileScreenExpandedPreview() {
    AriaTheme {
        ProfileScreen(
            state = ProfileUiState(
                isLoading = false,
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
            ),
            appVersionName = "1.0",
            onBack = {},
            onRetry = {},
            widthClass = WindowWidthClass.EXPANDED,
        )
    }
}
