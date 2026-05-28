package dev.jarkendar.photovault.core.network.api

import dev.jarkendar.photovault.core.network.auth.StubAuthTokenProvider
import dev.jarkendar.photovault.core.network.createPhotoVaultHttpClient
import dev.jarkendar.photovault.core.network.dto.tag.TagCreateRequestDto
import dev.jarkendar.photovault.core.network.dto.tag.TagUpdateRequestDto
import dev.jarkendar.photovault.core.network.error.NetworkError
import dev.jarkendar.photovault.core.network.fixtures.MockResponses
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class KtorTagsApiTest {

    private fun api(handler: MockRequestHandler) = KtorTagsApi(
        createPhotoVaultHttpClient(
            baseUrl = "http://test/v1",
            tokenProvider = StubAuthTokenProvider(),
            engine = MockEngine(handler),
        ),
    )

    private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")
    private val problemHeaders = headersOf(HttpHeaders.ContentType, "application/problem+json")

    @Test
    fun `listTags with usedOnly true sends correct query`() = runTest {
        var capturedUrl: String? = null
        val api = api { request ->
            capturedUrl = request.url.toString()
            respond("""{"items":[]}""", HttpStatusCode.OK, jsonHeaders)
        }
        api.listTags(usedOnly = true)
        assertTrue(capturedUrl!!.contains("usedOnly=true"))
    }

    @Test
    fun `createTag returns 201 with TagDto`() = runTest {
        val api = api { _ ->
            respond(
                """{"id":"tag-001","name":"#sunset","photoCount":0}""",
                HttpStatusCode.Created,
                headersOf(HttpHeaders.ContentType to listOf("application/json"), "Location" to listOf("/v1/tags/tag-001")),
            )
        }
        val result = api.createTag(TagCreateRequestDto("#sunset"))
        assertTrue(result.isSuccess)
        assertEquals("tag-001", result.getOrThrow().id)
    }

    @Test
    fun `createTag returns Conflict on 409 duplicate`() = runTest {
        val api = api { _ ->
            respond(MockResponses.PROBLEM_DUPLICATE_TAG, HttpStatusCode.Conflict, problemHeaders)
        }
        val result = api.createTag(TagCreateRequestDto("#morze"))
        val error = result.exceptionOrNull()
        assertIs<NetworkError.Conflict>(error)
    }

    @Test
    fun `patchTag returns updated tag on 200`() = runTest {
        val api = api { _ ->
            respond(
                """{"id":"tag-001","name":"#newname","photoCount":48}""",
                HttpStatusCode.OK,
                jsonHeaders,
            )
        }
        val result = api.patchTag("tag-001", TagUpdateRequestDto("#newname"))
        assertTrue(result.isSuccess)
        assertEquals("#newname", result.getOrThrow().name)
    }

    @Test
    fun `deleteTag returns success on 204`() = runTest {
        val api = api { _ -> respond("", HttpStatusCode.NoContent) }
        assertTrue(api.deleteTag("tag-001").isSuccess)
    }
}