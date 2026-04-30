package dev.jarkendar.photovault.core.network.api

import dev.jarkendar.photovault.core.network.auth.StubAuthTokenProvider
import dev.jarkendar.photovault.core.network.createPhotoVaultHttpClient
import dev.jarkendar.photovault.core.network.dto.auth.LoginRequestDto
import dev.jarkendar.photovault.core.network.dto.auth.RefreshRequestDto
import dev.jarkendar.photovault.core.network.error.NetworkError
import dev.jarkendar.photovault.core.network.fixtures.MockResponses
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class KtorAuthApiTest {

    private val authResponseJson = """
        {
          "accessToken":"acc-1",
          "refreshToken":"ref-1",
          "user":{"id":"user-jarek","username":"jarek","displayName":"Jarek"}
        }
    """.trimIndent()

    private fun mockEngine(
        status: HttpStatusCode,
        body: String,
        contentType: String = "application/json",
    ) = MockEngine { _ ->
        respond(content = body, status = status, headers = headersOf(HttpHeaders.ContentType, contentType))
    }

    private fun client(engine: MockEngine) = createPhotoVaultHttpClient(
        baseUrl = "http://test/v1",
        tokenProvider = StubAuthTokenProvider(),
        engine = engine,
    )

    @Test
    fun `login with valid credentials returns AuthResponseDto`() = runTest {
        val api = KtorAuthApi(client(mockEngine(HttpStatusCode.OK, authResponseJson)))
        val result = api.login(LoginRequestDto("jarek", "secret"))
        assertTrue(result.isSuccess)
        assertEquals("acc-1", result.getOrThrow().accessToken)
    }

    @Test
    fun `login with invalid credentials returns Unauthenticated`() = runTest {
        val api = KtorAuthApi(
            client(mockEngine(HttpStatusCode.Unauthorized, MockResponses.PROBLEM_INVALID_CREDENTIALS, "application/problem+json")),
        )
        val result = api.login(LoginRequestDto("x", "y"))
        assertTrue(result.isFailure)
        assertIs<NetworkError.Unauthenticated>(result.exceptionOrNull())
    }

    @Test
    fun `refresh returns new token pair`() = runTest {
        val api = KtorAuthApi(client(mockEngine(HttpStatusCode.OK, authResponseJson)))
        val result = api.refresh(RefreshRequestDto("ref-old"))
        assertTrue(result.isSuccess)
        assertEquals("ref-1", result.getOrThrow().refreshToken)
    }

    @Test
    fun `logout returns success on 204`() = runTest {
        val api = KtorAuthApi(client(mockEngine(HttpStatusCode.NoContent, "")))
        val result = api.logout()
        assertTrue(result.isSuccess)
    }

    @Test
    fun `me returns UserDto on 200`() = runTest {
        val api = KtorAuthApi(
            client(
                mockEngine(
                    HttpStatusCode.OK,
                    """{"id":"user-jarek","username":"jarek","displayName":"Jarek"}""",
                ),
            ),
        )
        val result = api.me()
        assertTrue(result.isSuccess)
        assertEquals("user-jarek", result.getOrThrow().id)
    }

    @Test
    fun `login does NOT include Authorization header`() = runTest {
        var capturedAuth: String? = "<unset>"
        val engine = MockEngine { request ->
            capturedAuth = request.headers[HttpHeaders.Authorization]
            respond(authResponseJson, HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/json"))
        }
        val api = KtorAuthApi(client(engine))
        api.login(LoginRequestDto("u", "p"))
        assertEquals(null, capturedAuth, "Authorization header must be absent for /auth/login")
    }

    @Test
    fun `me includes Authorization Bearer header`() = runTest {
        var capturedAuth: String? = null
        val engine = MockEngine { request ->
            capturedAuth = request.headers[HttpHeaders.Authorization]
            respond(
                """{"id":"user-jarek","username":"jarek","displayName":"Jarek"}""",
                HttpStatusCode.OK,
                headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val api = KtorAuthApi(client(engine))
        api.me()
        assertEquals("Bearer fake-token-stub", capturedAuth)
    }
}
