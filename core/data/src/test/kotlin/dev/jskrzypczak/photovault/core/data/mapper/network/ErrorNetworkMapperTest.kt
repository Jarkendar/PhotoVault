package dev.jarkendar.photovault.core.data.mapper.network

import dev.jarkendar.photovault.core.domain.error.DomainError
import dev.jarkendar.photovault.core.network.error.NetworkError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame

class ErrorNetworkMapperTest {

    @Test
    fun `NoConnectivity maps to NoConnectivity`() {
        assertSame(DomainError.NoConnectivity, NetworkError.NoConnectivity.toDomainError())
    }

    @Test
    fun `Timeout maps to NoConnectivity`() {
        assertSame(DomainError.NoConnectivity, NetworkError.Timeout.toDomainError())
    }

    @Test
    fun `NotFound maps to NotFound`() {
        val error = NetworkError.NotFound("photo-not-found").toDomainError()
        assertSame(DomainError.NotFound, error)
    }

    @Test
    fun `ServerError maps to ServerError with detail`() {
        val error = NetworkError.ServerError(status = 503, problemType = "x", detail = "scheduled maintenance").toDomainError()
        assertEquals(DomainError.ServerError(503, "scheduled maintenance"), error)
    }

    @Test
    fun `ServerError with null detail maps to empty message`() {
        val error = NetworkError.ServerError(status = 500, problemType = "x", detail = null).toDomainError()
        assertEquals(DomainError.ServerError(500, ""), error)
    }

    @Test
    fun `Unauthenticated maps to Unknown wrapping the network error`() {
        val source = NetworkError.Unauthenticated("invalid-token")
        val error = source.toDomainError()
        val unknown = assertIs<DomainError.Unknown>(error)
        assertSame(source, unknown.cause)
    }

    @Test
    fun `Forbidden maps to Unknown`() {
        val source = NetworkError.Forbidden("forbidden")
        assertIs<DomainError.Unknown>(source.toDomainError())
    }

    @Test
    fun `Conflict maps to Unknown`() {
        val source = NetworkError.Conflict("duplicate", detail = "duplicate")
        assertIs<DomainError.Unknown>(source.toDomainError())
    }

    @Test
    fun `ValidationFailed maps to Unknown`() {
        val source = NetworkError.ValidationFailed("validation-failed", errors = emptyMap())
        assertIs<DomainError.Unknown>(source.toDomainError())
    }

    @Test
    fun `Unknown with cause maps to Unknown with same cause`() {
        val cause = RuntimeException("oops")
        val error = NetworkError.Unknown(cause).toDomainError()
        val unknown = assertIs<DomainError.Unknown>(error)
        assertSame(cause, unknown.cause)
    }

    @Test
    fun `Unknown with null cause maps to Unknown wrapping the network error itself`() {
        val source = NetworkError.Unknown(cause = null)
        val error = source.toDomainError()
        val unknown = assertIs<DomainError.Unknown>(error)
        assertSame(source, unknown.cause)
    }
}