package dev.jskrzypczak.photovault.core.data.repository

import dev.jskrzypczak.photovault.core.data.mapper.network.toAuthDomainError
import dev.jskrzypczak.photovault.core.domain.model.AuthState
import dev.jskrzypczak.photovault.core.domain.model.AuthUser
import dev.jskrzypczak.photovault.core.domain.repository.AuthRepository
import dev.jskrzypczak.photovault.core.network.api.AuthApi
import dev.jskrzypczak.photovault.core.network.auth.TokenStore
import dev.jskrzypczak.photovault.core.network.dto.auth.LoginRequestDto
import dev.jskrzypczak.photovault.core.network.error.NetworkError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val tokenStore: TokenStore,
) : AuthRepository {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unknown)
    override val authState: Flow<AuthState> = _authState.asStateFlow()

    override suspend fun login(username: String, password: String): Result<Unit> {
        val result = authApi.login(LoginRequestDto(username, password))
        return result.fold(
            onSuccess = { dto ->
                tokenStore.save(dto.accessToken, dto.refreshToken)
                _authState.value = AuthState.Authenticated(
                    AuthUser(dto.user.id, dto.user.username, dto.user.displayName),
                )
                Result.success(Unit)
            },
            onFailure = { error ->
                Result.failure(error.toAuthDomainError())
            },
        )
    }

    override suspend fun logout() {
        runCatching { authApi.logout() }    // best-effort; always clear tokens
        tokenStore.clear()
        _authState.value = AuthState.Unauthenticated
    }

    override suspend fun refreshSession() {
        if (tokenStore.accessToken() == null) {
            _authState.value = AuthState.Unauthenticated
            return
        }
        authApi.me().fold(
            onSuccess = { user ->
                _authState.value = AuthState.Authenticated(
                    AuthUser(user.id, user.username, user.displayName),
                )
            },
            onFailure = { error ->
                if (error is NetworkError.Unauthenticated) {
                    // Expired or revoked — force re-login.
                    tokenStore.clear()
                }
                _authState.value = AuthState.Unauthenticated
            },
        )
    }
}
