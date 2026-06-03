package dev.jskrzypczak.photovault.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jskrzypczak.photovault.core.domain.error.DomainError
import dev.jskrzypczak.photovault.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onUsernameChange(value: String) {
        _uiState.update { it.copy(username = value, error = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, error = null) }
    }

    fun onLogin() {
        val state = _uiState.value
        if (state.isSubmitting) return
        _uiState.update { it.copy(isSubmitting = true, error = null) }
        viewModelScope.launch {
            val result = authRepository.login(state.username.trim(), state.password)
            result.fold(
                onSuccess = {
                    // authState flow in AuthRepository transitions to Authenticated;
                    // MainActivity observes it and navigates away automatically.
                    _uiState.update { it.copy(isSubmitting = false) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isSubmitting = false, error = error.toLoginError()) }
                },
            )
        }
    }

    private fun Throwable.toLoginError(): LoginError = when (this) {
        is DomainError.InvalidCredentials -> LoginError.INVALID_CREDENTIALS
        is DomainError.NoConnectivity -> LoginError.NETWORK
        else -> LoginError.UNKNOWN
    }
}
