package dev.jskrzypczak.photovault.core.domain.error

sealed class DomainError : Throwable() {
    data object NotFound : DomainError()
    data object NoConnectivity : DomainError()
    /** Login failed because the username or password is wrong. */
    data object InvalidCredentials : DomainError()
    /** The current session has expired or is invalid and the user must log in again. */
    data object Unauthenticated : DomainError()
    data class ServerError(val code: Int, override val message: String) : DomainError()
    data class Unknown(override val cause: Throwable) : DomainError()
}
