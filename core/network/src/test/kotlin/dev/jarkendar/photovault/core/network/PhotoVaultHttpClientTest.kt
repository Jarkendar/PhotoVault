package dev.jarkendar.photovault.core.network

import dev.jarkendar.photovault.core.network.auth.StubAuthTokenProvider
import dev.jarkendar.photovault.core.network.dto.health.HealthDto
import io.ktor.client.call.body
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PhotoVaultHttpClientTest {

    private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")

    @Test
    fun `client inserts Bearer header from tokenProvider on authenticated endpoints`() = runTest {
        var capturedAuth: String? = null
        val engine = MockEngine { request ->
            capturedAuth = request.headers[HttpHeaders.Authorization]
            respond("""{"id":"x","username":"u","displayName":"d"}""", HttpStatusCode.OK, jsonHeaders)
        }
        val client = createPhotoVaultHttpClient(
            baseUrl = "http://test/v1",
            tokenProvider = StubAuthTokenProvider(),
            engine = engine,
        )
        client.get("auth/me")
        assertEquals("Bearer fake-token-stub", capturedAuth)
    }

    @Test
    fun `client respects baseUrl`() = runTest {
        var capturedUrl: String? = null
        val engine = MockEngine { request ->
            capturedUrl = request.url.toString()
            respond("""{"status":"ok","version":"1.0.0"}""", HttpStatusCode.OK, jsonHeaders)
        }
        val client = createPhotoVaultHttpClient(
            baseUrl = "http://photovault.local/v1/",
            tokenProvider = StubAuthTokenProvider(),
            engine = engine,
        )
        client.get("health")
        assertTrue(capturedUrl!!.startsWith("http://photovault.local/v1"), "url=$capturedUrl")
    }

    @Test
    fun `client parses JSON response into DTO`() = runTest {
        val engine = MockEngine { _ ->
            respond("""{"status":"ok","version":"1.0.0"}""", HttpStatusCode.OK, jsonHeaders)
        }
        val client = createPhotoVaultHttpClient(
            baseUrl = "http://test/v1",
            tokenProvider = StubAuthTokenProvider(),
            engine = engine,
        )
        val response = client.get("/health")
        val dto: HealthDto = response.body()
        assertEquals("ok", dto.status)
    }

    @Test
    fun `client respects ignoreUnknownKeys`() = runTest {
        val engine = MockEngine { _ ->
            respond(
                """{"status":"ok","version":"1.0.0","extraField":"surprise","another":42}""",
                HttpStatusCode.OK,
                jsonHeaders,
            )
        }
        val client = createPhotoVaultHttpClient(
            baseUrl = "http://test/v1",
            tokenProvider = StubAuthTokenProvider(),
            engine = engine,
        )
        val dto: HealthDto = client.get("/health").body()
        assertEquals("ok", dto.status)
        assertEquals("1.0.0", dto.version)
    }
}
