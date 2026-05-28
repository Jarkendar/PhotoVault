package dev.jarkendar.photovault.core.network.error

sealed class NetworkError : Throwable() {
    data object NoConnectivity : NetworkError()
    data object Timeout : NetworkError()
    data class Unauthenticated(val problemType: String) : NetworkError()
    data class Forbidden(val problemType: String) : NetworkError()
    data class NotFound(val problemType: String) : NetworkError()
    data class Conflict(val problemType: String, val detail: String?) : NetworkError()
    data class ValidationFailed(
        val problemType: String,
        val errors: Map<String, List<String>>,
    ) : NetworkError()
    data class ServerError(
        val status: Int,
        val problemType: String,
        val detail: String?,
    ) : NetworkError()
    data class Unknown(override val cause: Throwable?) : NetworkError()
}