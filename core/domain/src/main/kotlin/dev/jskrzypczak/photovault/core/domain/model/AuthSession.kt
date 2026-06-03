package dev.jskrzypczak.photovault.core.domain.model

/** Represents the currently authenticated user. */
data class AuthUser(
    val id: String,
    val username: String,
    val displayName: String,
)

/** Represents the current authentication state observed by the UI layer. */
sealed interface AuthState {
    /** Initial state — the app has not yet verified whether a session exists. */
    data object Unknown : AuthState

    /** A valid session is present and the user is known. */
    data class Authenticated(val user: AuthUser) : AuthState

    /** No valid session — the user must log in. */
    data object Unauthenticated : AuthState
}
