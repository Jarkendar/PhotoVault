package dev.jskrzypczak.photovault.core.domain.repository

import dev.jskrzypczak.photovault.core.domain.model.AuthState
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    /** Hot flow of the current authentication state; initial value is [AuthState.Unknown]. */
    val authState: Flow<AuthState>

    /**
     * Attempts to log in with [username] and [password].
     * On success, persists tokens and transitions [authState] to [AuthState.Authenticated].
     * On failure, returns a [Result] containing a [dev.jskrzypczak.photovault.core.domain.error.DomainError].
     */
    suspend fun login(username: String, password: String): Result<Unit>

    /**
     * Logs out the current user: calls the server (best-effort), clears stored tokens,
     * and transitions [authState] to [AuthState.Unauthenticated].
     */
    suspend fun logout()

    /**
     * Verifies the stored session by calling GET /auth/me.
     * - No tokens → [AuthState.Unauthenticated].
     * - Server returns user → [AuthState.Authenticated].
     * - Server returns 401 → clears tokens + [AuthState.Unauthenticated].
     *
     * Should be called once on app startup before showing the main UI.
     */
    suspend fun refreshSession()
}
