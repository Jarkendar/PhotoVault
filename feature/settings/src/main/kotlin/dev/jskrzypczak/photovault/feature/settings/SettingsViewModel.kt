package dev.jskrzypczak.photovault.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jskrzypczak.photovault.core.domain.repository.ServerSettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val serverSettingsRepository: ServerSettingsRepository,
) : ViewModel() {

    private val _isConnected = MutableStateFlow(false)

    val uiState = combine(
        serverSettingsRepository.serverUrl,
        _isConnected,
    ) { url, connected ->
        SettingsUiState(serverAddress = url, isConnected = connected)
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
            // URL change triggers collect above, but checkHealth() after setServerUrl needs
            // the new URL to be cached first — the DataStore flow handles the ordering.
        }
    }
}
