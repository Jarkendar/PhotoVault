package dev.jarkendar.photovault.core.domain.error

sealed class DomainError : Throwable() {
    data object NotFound : DomainError()
    data object NoConnectivity : DomainError()
    data class ServerError(val code: Int, override val message: String) : DomainError()
    data class Unknown(override val cause: Throwable) : DomainError()
}
