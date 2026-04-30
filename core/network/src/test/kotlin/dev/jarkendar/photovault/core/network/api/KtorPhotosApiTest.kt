package dev.jarkendar.photovault.core.network.api

import dev.jarkendar.photovault.core.network.auth.StubAuthTokenProvider
import dev.jarkendar.photovault.core.network.createPhotoVaultHttpClient
import dev.jarkendar.photovault.core.network.dto.photo.PhotoPatchRequestDto
import dev.jarkendar.photovault.core.network.error.NetworkError
import dev.jarkendar.photovault.core.network.fixtures.MockResponses
import dev.jarkendar.photovault.core.network.fixtures.PhotoDtoFixtures
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class KtorPhotosApiTest {

    private fun api(handler: MockRequestHandler): KtorPhotosApi = KtorPhotosApi(
        createPhotoVaultHttpClient(
            baseUrl = "http://test/v1",
            tokenProvider = StubAuthTokenProvider(),
            engine = MockEngine(handler),
        ),
    )

    private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")
    private val problemHeaders = headersOf(HttpHeaders.ContentType, "application/problem+json")

    @Test
    fun `listPhotos returns PhotoPageDto on 200`() = runTest {
        val api = api { _ ->
            respond(PhotoDtoFixtures.SAMPLE_PHOTO_PAGE_JSON, HttpStatusCode.OK, jsonHeaders)
        }
        val result = api.listPhotos()
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrThrow().items.size)
    }

    @Test
    fun `listPhotos passes cursor and limit query params`() = runTest {
        var capturedUrl: String? = null
        val api = api { request ->
            capturedUrl = request.url.toString()
            respond(PhotoDtoFixtures.EMPTY_PHOTO_PAGE_JSON, HttpStatusCode.OK, jsonHeaders)
        }
        api.listPhotos(cursor = "abc", limit = 50)
        assertTrue(capturedUrl!!.contains("cursor=abc"), "url=$capturedUrl")
        assertTrue(capturedUrl!!.contains("limit=50"))
    }

    @Test
    fun `listPhotos passes tagIds as comma-separated query param`() = runTest {
        var capturedUrl: String? = null
        val api = api { request ->
            capturedUrl = request.url.toString()
            respond(PhotoDtoFixtures.EMPTY_PHOTO_PAGE_JSON, HttpStatusCode.OK, jsonHeaders)
        }
        api.listPhotos(tagIds = listOf("tag-1", "tag-2"))
        assertTrue(
            capturedUrl!!.contains("tagIds=tag-1%2Ctag-2") || capturedUrl!!.contains("tagIds=tag-1,tag-2"),
            "url=$capturedUrl",
        )
    }

    @Test
    fun `listPhotos with uploadedBy=me sends correct query`() = runTest {
        var capturedUrl: String? = null
        val api = api { request ->
            capturedUrl = request.url.toString()
            respond(PhotoDtoFixtures.EMPTY_PHOTO_PAGE_JSON, HttpStatusCode.OK, jsonHeaders)
        }
        api.listPhotos(uploadedBy = "me")
        assertTrue(capturedUrl!!.contains("uploadedBy=me"))
    }

    @Test
    fun `listPhotos returns Unauthenticated on 401`() = runTest {
        val api = api { _ ->
            respond(MockResponses.PROBLEM_INVALID_TOKEN, HttpStatusCode.Unauthorized, problemHeaders)
        }
        val result = api.listPhotos()
        assertTrue(result.isFailure)
        assertIs<NetworkError.Unauthenticated>(result.exceptionOrNull())
    }

    @Test
    fun `listPhotos returns NoConnectivity on IOException`() = runTest {
        val api = api { _ -> throw IOException("network down") }
        val result = api.listPhotos()
        assertTrue(result.isFailure)
        assertEquals(NetworkError.NoConnectivity, result.exceptionOrNull())
    }

    @Test
    fun `getPhoto returns PhotoDto on 200`() = runTest {
        val api = api { _ ->
            respond(PhotoDtoFixtures.SAMPLE_PHOTO_JSON, HttpStatusCode.OK, jsonHeaders)
        }
        val result = api.getPhoto("photo-abc123")
        assertTrue(result.isSuccess)
        assertEquals("photo-abc123", result.getOrThrow().id)
    }

    @Test
    fun `getPhoto returns NotFound on 404`() = runTest {
        val api = api { _ ->
            respond(MockResponses.PROBLEM_NOT_FOUND, HttpStatusCode.NotFound, problemHeaders)
        }
        val result = api.getPhoto("missing")
        assertIs<NetworkError.NotFound>(result.exceptionOrNull())
    }

    @Test
    fun `patchPhoto sends body and returns updated photo`() = runTest {
        var capturedMethod: HttpMethod? = null
        var capturedBodyText = ""
        val api = api { request ->
            capturedMethod = request.method
            capturedBodyText = (request.body as? OutgoingContent.ByteArrayContent)
                ?.bytes()?.toString(Charsets.UTF_8).orEmpty()
            respond(PhotoDtoFixtures.SAMPLE_PHOTO_JSON, HttpStatusCode.OK, jsonHeaders)
        }
        val result = api.patchPhoto("photo-abc123", PhotoPatchRequestDto(isFavorite = true))
        assertTrue(result.isSuccess)
        assertEquals(HttpMethod.Patch, capturedMethod)
        assertTrue(capturedBodyText.contains("\"isFavorite\""), "body=$capturedBodyText")
    }

    @Test
    fun `deletePhoto returns success on 204`() = runTest {
        val api = api { _ -> respond("", HttpStatusCode.NoContent) }
        val result = api.deletePhoto("photo-abc123")
        assertTrue(result.isSuccess)
    }
}