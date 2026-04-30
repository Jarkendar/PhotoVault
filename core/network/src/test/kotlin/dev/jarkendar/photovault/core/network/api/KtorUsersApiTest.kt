package dev.jarkendar.photovault.core.network.api

import dev.jarkendar.photovault.core.network.auth.StubAuthTokenProvider
import dev.jarkendar.photovault.core.network.createPhotoVaultHttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KtorUsersApiTest {

    private fun api(handler: MockRequestHandler) = KtorUsersApi(
        createPhotoVaultHttpClient(
            baseUrl = "http://test/v1",
            tokenProvider = StubAuthTokenProvider(),
            engine = MockEngine(handler),
        ),
    )

    @Test
    fun `listUsers returns UserListDto`() = runTest {
        val api = api { _ ->
            respond(
                """{"items":[{"id":"user-jarek","username":"jarek","displayName":"Jarek"}]}""",
                HttpStatusCode.OK,
                headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val result = api.listUsers()
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrThrow().items.size)
    }
}