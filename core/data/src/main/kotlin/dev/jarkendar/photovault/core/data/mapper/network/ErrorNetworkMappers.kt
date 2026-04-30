package dev.jarkendar.photovault.core.data.mapper.network

import dev.jarkendar.photovault.core.domain.error.DomainError
import dev.jarkendar.photovault.core.network.error.NetworkError

internal fun NetworkError.toDomainError(): DomainError = when (this) {
    is NetworkError.NoConnectivity -> DomainError.NoConnectivity
    is NetworkError.Timeout -> DomainError.NoConnectivity
    is NetworkError.NotFound -> DomainError.NotFound
    is NetworkError.ServerError -> DomainError.ServerError(status, detail.orEmpty())
    is NetworkError.Unauthenticated -> DomainError.Unknown(this)
    is NetworkError.Forbidden -> DomainError.Unknown(this)
    is NetworkError.Conflict -> DomainError.Unknown(this)
    is NetworkError.ValidationFailed -> DomainError.Unknown(this)
    is NetworkError.Unknown -> DomainError.Unknown(cause ?: this)
}