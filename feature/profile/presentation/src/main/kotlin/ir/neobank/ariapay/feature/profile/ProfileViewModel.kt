package ir.neobank.ariapay.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.neobank.ariapay.core.session.SessionManager
import ir.neobank.ariapay.feature.profile.contract.ProfileEffect
import ir.neobank.ariapay.feature.profile.contract.ProfileEvent
import ir.neobank.ariapay.feature.profile.contract.ProfileUiError
import ir.neobank.ariapay.feature.profile.contract.ProfileUiState
import ir.neobank.ariapay.feature.profile.domain.GetProfileUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val retryRequest = MutableStateFlow(0)

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _effect = Channel<ProfileEffect>(capacity = Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        observeProfile()
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            ProfileEvent.RetryClicked -> retryRequest.update { it + 1 }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeProfile() {
        viewModelScope.launch {
            sessionManager.currentUserId
                .onEach { userId ->
                    if (userId.isNullOrBlank()) {
                        _effect.trySend(ProfileEffect.NavigateToAuthentication)
                        _uiState.value = ProfileUiState(
                            isLoading = false,
                            error = ProfileUiError.AUTHENTICATION_REQUIRED,
                        )
                    } else {
                        // A new account must never briefly see the previous account's profile.
                        _uiState.value = ProfileUiState(isLoading = true)
                    }
                }
                .flatMapLatest { userId ->
                    if (userId.isNullOrBlank()) {
                        flowOf(
                            ProfileUiState(
                                isLoading = false,
                                error = ProfileUiError.AUTHENTICATION_REQUIRED,
                            ),
                        )
                    } else {
                        retryRequest.flatMapLatest {
                            getProfileUseCase(userId)
                                .map { profile ->
                                    ProfileUiState(
                                        isLoading = false,
                                        profile = profile,
                                    )
                                }
                                .onStart {
                                    emit(
                                        _uiState.value.copy(
                                            isLoading = true,
                                            error = null,
                                        ),
                                    )
                                }
                                .catch {
                                    emit(
                                        _uiState.value.copy(
                                            isLoading = false,
                                            error = ProfileUiError.PROFILE_LOAD_FAILED,
                                        ),
                                    )
                                }
                        }
                    }
                }
                .collect { state -> _uiState.value = state }
        }
    }
}
