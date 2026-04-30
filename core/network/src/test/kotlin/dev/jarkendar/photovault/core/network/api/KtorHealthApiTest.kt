package dev.jarkendar.photovault.core.network.api

import dev.jarkendar.photovault.core.network.auth.StubAuthTokenProvider
import dev.jarkendar.photovault.core.network.createPhotoVaultHttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class KtorHealthApiTest {

    @Test
    fun `getHealth returns HealthDto on 200`() = runTest {
        val engine = MockEngine { _ ->
            respond(
                content = """{"status":"ok","version":"1.0.0"}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val client = createPhotoVaultHttpClient(
            baseUrl = "http://test/v1",
            tokenProvider = StubAuthTokenProvider(),
            engine = engine,
        )
        val api = KtorHealthApi(client)

        val result = api.getHealth()

        assertTrue(result.isSuccess)
        assertEquals("ok", result.getOrThrow().status)
        assertEquals("1.0.0", result.getOrThrow().version)
    }

    @Test
    fun `getHealth does NOT include Authorization header`() = runTest {
        var capturedAuthHeader: String? = "<unset>"
        val engine = MockEngine { request ->
            capturedAuthHeader = request.headers[HttpHeaders.Authorization]
            respond(
                content = """{"status":"ok","version":"1.0.0"}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val client = createPhotoVaultHttpClient(
            baseUrl = "http://test/v1",
            tokenProvider = StubAuthTokenProvider(),
            engine = engine,
        )
        val api = KtorHealthApi(client)

        api.getHealth()

        assertNull(capturedAuthHeader, "Authorization header must be absent for /health")
    }
}
