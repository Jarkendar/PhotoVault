package dev.jskrzypczak.photovault.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jskrzypczak.photovault.core.domain.model.AuthState
import dev.jskrzypczak.photovault.core.domain.repository.AuthRepository
import dev.jskrzypczak.photovault.core.domain.repository.ServerSettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val serverSettingsRepository: ServerSettingsRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _isConnected = MutableStateFlow(false)

    val uiState = combine(
        serverSettingsRepository.serverUrl,
        _isConnected,
        authRepository.authState,
    ) { url, connected, authState ->
        val summary = when (authState) {
            is AuthState.Authenticated -> authState.user.displayName
            AuthState.Unauthenticated -> ""
            AuthState.Unknown -> ""
        }
        SettingsUiState(serverAddress = url, isConnected = connected, authSummary = summary)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState(),
    )

    init {
        // Check health on startup and re-check whenever the URL changes.
        viewModelScope.launch {
            serverSettingsRepository.serverUrl.collect { _ ->
                _isConnected.value = serverSettingsRepository.checkHealth()
            }
        }
    }

    fun onServerUrlChange(url: String) {
        viewModelScope.launch {
            serverSettingsRepository.setServerUrl(url)
        }
    }

    fun onLogout() {
        viewModelScope.launch {
            authRepository.logout()
            // authState transitions to Unauthenticated; MainActivity observes and shows LoginScreen.
        }
    }
}
