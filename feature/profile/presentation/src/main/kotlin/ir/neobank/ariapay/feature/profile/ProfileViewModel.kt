package ir.neobank.ariapay.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.neobank.ariapay.core.common.result.AriaResult
import ir.neobank.ariapay.core.common.settings.AppThemeMode
import ir.neobank.ariapay.core.common.settings.AppThemeSettings
import ir.neobank.ariapay.core.session.SessionManager
import ir.neobank.ariapay.feature.profile.contract.ProfileActionError
import ir.neobank.ariapay.feature.profile.contract.ProfileEffect
import ir.neobank.ariapay.feature.profile.contract.ProfileEvent
import ir.neobank.ariapay.feature.profile.contract.ProfileUiError
import ir.neobank.ariapay.feature.profile.contract.ProfileUiState
import ir.neobank.ariapay.feature.profile.domain.usecase.GetProfileUseCase
import ir.neobank.ariapay.feature.profile.domain.usecase.LogoutUseCase
import ir.neobank.ariapay.feature.profile.domain.usecase.UpdateNameUseCase
import ir.neobank.ariapay.feature.profile.domain.usecase.UpdateProfilePhotoUseCase
import ir.neobank.ariapay.feature.profile.domain.model.UserProfile
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateNameUseCase: UpdateNameUseCase,
    private val updateProfilePhotoUseCase: UpdateProfilePhotoUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val appThemeSettings: AppThemeSettings,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val retryRequest = MutableStateFlow(0)

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _effect = Channel<ProfileEffect>(capacity = Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        observeProfile()
        observeTheme()
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            ProfileEvent.RetryClicked -> retryRequest.update { it + 1 }
            ProfileEvent.EditNameClicked -> openNameEditor()
            is ProfileEvent.NameDraftChanged -> _uiState.update {
                it.copy(nameDraft = event.value, nameError = null)
            }
            ProfileEvent.DismissNameDialog -> dismissNameEditor()
            ProfileEvent.SaveNameClicked -> saveName()
            is ProfileEvent.ProfilePhotoSelected -> updateProfilePhoto(event.uri)
            is ProfileEvent.ThemeModeSelected -> updateTheme(event.mode)
            ProfileEvent.LogoutClicked -> _uiState.update {
                it.copy(isLogoutDialogVisible = true, logoutError = null)
            }
            ProfileEvent.DismissLogoutDialog -> _uiState.update {
                if (it.isLoggingOut) it else it.copy(isLogoutDialogVisible = false, logoutError = null)
            }
            ProfileEvent.ConfirmLogoutClicked -> logout()
        }
    }

    private fun openNameEditor() {
        _uiState.update { state ->
            if (state.profile == null || state.isSavingName) state
            else state.copy(
                isNameDialogVisible = true,
                nameDraft = state.profile.fullName.orEmpty(),
                nameError = null,
            )
        }
    }

    private fun dismissNameEditor() {
        _uiState.update { state ->
            if (state.isSavingName) state
            else state.copy(isNameDialogVisible = false, nameError = null)
        }
    }

    private fun saveName() {
        val state = _uiState.value
        val userId = state.profile?.userId ?: return
        if (state.isSavingName) return
        if (state.nameDraft.isBlank()) {
            _uiState.update { it.copy(nameError = ProfileActionError.NAME_REQUIRED) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSavingName = true, nameError = null) }
            try {
                when (updateNameUseCase(userId, state.nameDraft)) {
                    is AriaResult.Success -> _uiState.update {
                        it.copy(isSavingName = false, isNameDialogVisible = false, nameError = null)
                    }
                    is AriaResult.Error -> _uiState.update {
                        it.copy(isSavingName = false, nameError = ProfileActionError.NAME_SAVE_FAILED)
                    }
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _uiState.update { it.copy(isSavingName = false, nameError = ProfileActionError.NAME_SAVE_FAILED) }
            }
        }
    }

    private fun updateProfilePhoto(uri: String) {
        val userId = _uiState.value.profile?.userId ?: return
        if (_uiState.value.isUpdatingPhoto) return

        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingPhoto = true, photoError = null) }
            try {
                when (updateProfilePhotoUseCase(userId, uri)) {
                    is AriaResult.Success -> _uiState.update {
                        it.copy(isUpdatingPhoto = false, photoError = null)
                    }
                    is AriaResult.Error -> _uiState.update {
                        it.copy(isUpdatingPhoto = false, photoError = ProfileActionError.PHOTO_SAVE_FAILED)
                    }
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _uiState.update { it.copy(isUpdatingPhoto = false, photoError = ProfileActionError.PHOTO_SAVE_FAILED) }
            }
        }
    }

    private fun updateTheme(mode: AppThemeMode) {
        if (_uiState.value.isSavingTheme || _uiState.value.themeMode == mode) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSavingTheme = true, themeError = null) }
            try {
                appThemeSettings.setThemeMode(mode)
                _uiState.update { it.copy(isSavingTheme = false) }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(isSavingTheme = false, themeError = ProfileActionError.THEME_SAVE_FAILED)
                }
            }
        }
    }

    private fun logout() {
        if (_uiState.value.isLoggingOut) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoggingOut = true, logoutError = null) }
            try {
                logoutUseCase()
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(isLoggingOut = false, logoutError = ProfileActionError.LOGOUT_FAILED)
                }
            }
        }
    }

    private fun observeTheme() {
        viewModelScope.launch {
            appThemeSettings.themeMode
                .catch {
                    _uiState.update { it.copy(themeError = ProfileActionError.THEME_SAVE_FAILED) }
                }
                .collect { mode -> _uiState.update { it.copy(themeMode = mode) } }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeProfile() {
        viewModelScope.launch {
            sessionManager.currentUserId
                .onEach { userId ->
                    if (userId.isNullOrBlank()) {
                        _uiState.value = ProfileUiState(
                            isLoading = false,
                            isAuthenticated = false,
                            error = ProfileUiError.AUTHENTICATION_REQUIRED,
                            themeMode = _uiState.value.themeMode,
                        )
                        _effect.trySend(ProfileEffect.NavigateToAuthentication)
                    } else {
                        _uiState.value = ProfileUiState(
                            isLoading = true,
                            isAuthenticated = true,
                            themeMode = _uiState.value.themeMode,
                        )
                    }
                }
                .flatMapLatest { userId ->
                    if (userId.isNullOrBlank()) {
                        flowOf(ProfileLoadResult.AuthenticationRequired)
                    } else {
                        retryRequest.flatMapLatest {
                            getProfileUseCase(userId)
                                .map<UserProfile?, ProfileLoadResult> { ProfileLoadResult.Loaded(it) }
                                .onStart {
                                    _uiState.update { it.copy(isLoading = true, error = null) }
                                }
                                .catch { emit(ProfileLoadResult.Failed) }
                        }
                    }
                }
                .collect { result ->
                    when (result) {
                        is ProfileLoadResult.Loaded -> _uiState.update {
                            it.copy(isLoading = false, profile = result.profile, error = null)
                        }
                        ProfileLoadResult.Failed -> _uiState.update {
                            it.copy(isLoading = false, error = ProfileUiError.PROFILE_LOAD_FAILED)
                        }
                        ProfileLoadResult.AuthenticationRequired -> Unit
                    }
                }
        }
    }

    private sealed interface ProfileLoadResult {
        data class Loaded(val profile: UserProfile?) : ProfileLoadResult
        data object Failed : ProfileLoadResult
        data object AuthenticationRequired : ProfileLoadResult
    }
}
