package dev.jskrzypczak.photovault.core.data.mapper.network

import dev.jskrzypczak.photovault.core.domain.error.DomainError
import dev.jskrzypczak.photovault.core.network.error.NetworkError

/** Maps a [NetworkError] to the closest [DomainError] for general (non-auth) operations. */
internal fun NetworkError.toDomainError(): DomainError = when (this) {
    is NetworkError.NoConnectivity -> DomainError.NoConnectivity
    is NetworkError.Timeout -> DomainError.NoConnectivity
    is NetworkError.NotFound -> DomainError.NotFound
    is NetworkError.ServerError -> DomainError.ServerError(status, detail.orEmpty())
    is NetworkError.Unauthenticated -> DomainError.Unauthenticated
    is NetworkError.Forbidden -> DomainError.Unknown(this)
    is NetworkError.Conflict -> DomainError.Unknown(this)
    is NetworkError.ValidationFailed -> DomainError.Unknown(this)
    is NetworkError.Unknown -> DomainError.Unknown(cause ?: this)
}

/**
 * Maps any [Throwable] to a [DomainError] suitable for **authentication operations**.
 *
 * - `invalid-credentials` 401 → [DomainError.InvalidCredentials] (wrong username/password)
 * - other 401 → [DomainError.Unauthenticated] (expired token, revoked session)
 * - everything else → delegates to [toDomainError] or wraps as [DomainError.Unknown]
 */
internal fun Throwable.toAuthDomainError(): DomainError = when {
    this is NetworkError.Unauthenticated && problemType.contains("invalid-credentials") ->
        DomainError.InvalidCredentials
    this is NetworkError -> toDomainError()
    else -> DomainError.Unknown(this)
}
