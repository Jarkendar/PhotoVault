package dev.jskrzypczak.photovault.core.network

import dev.jskrzypczak.photovault.core.network.auth.StubTokenStore
import dev.jskrzypczak.photovault.core.network.dto.health.HealthDto
import io.ktor.client.call.body
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
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
            tokenStore = StubTokenStore(),
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
            tokenStore = StubTokenStore(),
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
            tokenStore = StubTokenStore(),
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
            tokenStore = StubTokenStore(),
            engine = engine,
        )
        val dto: HealthDto = client.get("/health").body()
        assertEquals("ok", dto.status)
        assertEquals("1.0.0", dto.version)
    }

    @Test
    fun `401 on protected endpoint triggers refreshTokens and retries with new Bearer`() = runTest {
        val store = StubTokenStore(access = "old-access", refresh = "old-refresh")
        var callCount = 0
        val capturedAuthHeaders = mutableListOf<String?>()

        val engine = MockEngine { request ->
            callCount++
            capturedAuthHeaders += request.headers[HttpHeaders.Authorization]
            val path = request.url.encodedPath
            when {
                // Refresh endpoint — return new token pair.
                path.endsWith("/auth/refresh") -> respond(
                    """{"accessToken":"new-access","refreshToken":"new-refresh","user":{"id":"u","username":"u","displayName":"U"}}""",
                    HttpStatusCode.OK,
                    jsonHeaders,
                )
                // First call to /photos → 401; second call (after refresh) → 200.
                callCount == 1 -> respond(
                    """{"type":"https://photovault.local/errors/invalid-token","status":401}""",
                    HttpStatusCode.Unauthorized,
                    headersOf(HttpHeaders.ContentType, "application/problem+json"),
                )
                else -> respond("""{"items":[],"total":0,"page":1,"pageSize":20}""", HttpStatusCode.OK, jsonHeaders)
            }
        }

        val client = createPhotoVaultHttpClient(baseUrl = "http://test/v1", tokenStore = store, engine = engine)
        val response = client.get("photos")
        assertEquals(HttpStatusCode.OK, response.status)
        // After refresh the store holds the new tokens.
        assertEquals("new-access", store.accessToken())
        assertEquals("new-refresh", store.refreshToken())
    }

    @Test
    fun `failed refresh clears tokens and does not loop`() = runTest {
        val store = StubTokenStore(access = "old-access", refresh = "old-refresh")

        val engine = MockEngine { request ->
            val path = request.url.encodedPath
            if (path.endsWith("/auth/refresh")) {
                respond(
                    """{"type":"https://photovault.local/errors/unauthenticated","status":401}""",
                    HttpStatusCode.Unauthorized,
                    headersOf(HttpHeaders.ContentType, "application/problem+json"),
                )
            } else {
                respond(
                    """{"type":"https://photovault.local/errors/invalid-token","status":401}""",
                    HttpStatusCode.Unauthorized,
                    headersOf(HttpHeaders.ContentType, "application/problem+json"),
                )
            }
        }

        val client = createPhotoVaultHttpClient(baseUrl = "http://test/v1", tokenStore = store, engine = engine)
        client.get("photos")
        // Tokens must be cleared so the app forces re-login.
        assertNull(store.accessToken())
        assertNull(store.refreshToken())
    }
}
