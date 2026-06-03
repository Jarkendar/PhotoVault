package dev.jskrzypczak.photovault

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jskrzypczak.photovault.core.domain.model.AuthState
import dev.jskrzypczak.photovault.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Lightweight ViewModel that survives configuration changes and coordinates the initial
 * session check ([AuthRepository.refreshSession]) and the observable [authState] used
 * by [MainActivity] to gate access to the main NavHost.
 */
class AuthGateViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    val authState: StateFlow<AuthState> = authRepository.authState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = AuthState.Unknown,
    )

    init {
        viewModelScope.launch {
            authRepository.refreshSession()
        }
    }
}
