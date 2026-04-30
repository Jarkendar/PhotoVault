package dev.jarkendar.photovault.core.network.error

import dev.jarkendar.photovault.core.network.fixtures.MockResponses
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertSame

class NetworkErrorMapperTest {

    private suspend fun responseFor(status: HttpStatusCode, body: String): HttpResponse {
        val engine = MockEngine { _ ->
            respond(
                content = body,
                status = status,
                headers = headersOf(HttpHeaders.ContentType, "application/problem+json"),
            )
        }
        val client = HttpClient(engine)
        return client.get("/x")
    }

    @Test
    fun `401 invalid-token maps to Unauthenticated`() = runTest {
        val response = responseFor(HttpStatusCode.Unauthorized, MockResponses.PROBLEM_INVALID_TOKEN)
        val error = mapToNetworkError(response)
        assertIs<NetworkError.Unauthenticated>(error)
        assertEquals("https://photovault.local/errors/invalid-token", error.problemType)
    }

    @Test
    fun `401 invalid-credentials maps to Unauthenticated`() = runTest {
        val response = responseFor(HttpStatusCode.Unauthorized, MockResponses.PROBLEM_INVALID_CREDENTIALS)
        val error = mapToNetworkError(response)
        assertIs<NetworkError.Unauthenticated>(error)
    }

    @Test
    fun `403 maps to Forbidden`() = runTest {
        val response = responseFor(HttpStatusCode.Forbidden, MockResponses.PROBLEM_FORBIDDEN)
        val error = mapToNetworkError(response)
        assertIs<NetworkError.Forbidden>(error)
    }

    @Test
    fun `404 maps to NotFound`() = runTest {
        val response = responseFor(HttpStatusCode.NotFound, MockResponses.PROBLEM_NOT_FOUND)
        val error = mapToNetworkError(response)
        assertIs<NetworkError.NotFound>(error)
        assertEquals("https://photovault.local/errors/photo-not-found", error.problemType)
    }

    @Test
    fun `409 maps to Conflict and preserves detail`() = runTest {
        val response = responseFor(HttpStatusCode.Conflict, MockResponses.PROBLEM_DUPLICATE_TAG)
        val error = mapToNetworkError(response)
        assertIs<NetworkError.Conflict>(error)
        assertEquals("A tag with name '#morze' already exists", error.detail)
    }

    @Test
    fun `400 with validation-failed and errors maps to ValidationFailed`() = runTest {
        val response = responseFor(HttpStatusCode.BadRequest, MockResponses.PROBLEM_VALIDATION_FAILED)
        val error = mapToNetworkError(response)
        assertIs<NetworkError.ValidationFailed>(error)
        assertEquals(2, error.errors.size)
        assertNotNull(error.errors["name"])
    }

    @Test
    fun `400 without errors map maps to ValidationFailed with empty map`() = runTest {
        val response = responseFor(HttpStatusCode.BadRequest, MockResponses.PROBLEM_VALIDATION_FAILED_NO_ERRORS)
        val error = mapToNetworkError(response)
        assertIs<NetworkError.ValidationFailed>(error)
        assertEquals(emptyMap(), error.errors)
    }

    @Test
    fun `500 maps to ServerError 500`() = runTest {
        val response = responseFor(HttpStatusCode.InternalServerError, MockResponses.PROBLEM_INTERNAL)
        val error = mapToNetworkError(response)
        assertIs<NetworkError.ServerError>(error)
        assertEquals(500, error.status)
    }

    @Test
    fun `503 maps to ServerError 503`() = runTest {
        val response = responseFor(HttpStatusCode.ServiceUnavailable, MockResponses.PROBLEM_SERVICE_UNAVAILABLE)
        val error = mapToNetworkError(response)
        assertIs<NetworkError.ServerError>(error)
        assertEquals(503, error.status)
    }

    @Test
    fun `IOException maps to NoConnectivity`() {
        val error = mapToNetworkError(IOException("dns"))
        assertSame(NetworkError.NoConnectivity, error)
    }

    @Test
    fun `HttpRequestTimeoutException maps to Timeout`() {
        val error = mapToNetworkError(HttpRequestTimeoutException("/x", 1000L))
        assertSame(NetworkError.Timeout, error)
    }

    @Test
    fun `SerializationException maps to Unknown`() {
        val cause = SerializationException("bad json")
        val error = mapToNetworkError(cause)
        assertIs<NetworkError.Unknown>(error)
        assertSame(cause, error.cause)
    }

    @Test
    fun `Random Exception maps to Unknown`() {
        val cause = RuntimeException("nope")
        val error = mapToNetworkError(cause)
        assertIs<NetworkError.Unknown>(error)
        assertSame(cause, error.cause)
    }
}
