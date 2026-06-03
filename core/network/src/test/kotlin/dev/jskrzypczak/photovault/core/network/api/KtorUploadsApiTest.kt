package dev.jskrzypczak.photovault.core.network.api

import dev.jskrzypczak.photovault.core.network.auth.StubTokenStore
import dev.jskrzypczak.photovault.core.network.createPhotoVaultHttpClient
import dev.jskrzypczak.photovault.core.network.dto.upload.UploadStatus
import dev.jskrzypczak.photovault.core.network.error.NetworkError
import dev.jskrzypczak.photovault.core.network.fixtures.MockResponses
import dev.jskrzypczak.photovault.core.network.fixtures.UploadDtoFixtures
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class KtorUploadsApiTest {

    private fun api(handler: MockRequestHandler) = KtorUploadsApi(
        createPhotoVaultHttpClient(
            baseUrl = "http://test/v1",
            tokenStore = StubTokenStore(),
            engine = MockEngine(handler),
        ),
    )

    private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")
    private val problemHeaders = headersOf(HttpHeaders.ContentType, "application/problem+json")

    @Test
    fun `listUploads returns UploadListDto`() = runTest {
        val api = api { _ ->
            respond(
                """{"items":[${UploadDtoFixtures.UPLOAD_DONE_JSON}]}""",
                HttpStatusCode.OK,
                jsonHeaders,
            )
        }
        val result = api.listUploads()
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrThrow().items.size)
    }

    @Test
    fun `listUploads with statuses sends comma-separated query`() = runTest {
        var capturedUrl: String? = null
        val api = api { request ->
            capturedUrl = request.url.toString()
            respond("""{"items":[]}""", HttpStatusCode.OK, jsonHeaders)
        }
        api.listUploads(statuses = listOf(UploadStatus.PROCESSING, UploadStatus.UPLOADING))
        assertTrue(
            capturedUrl!!.contains("status=processing%2Cuploading") ||
                capturedUrl!!.contains("status=processing,uploading"),
            "url=$capturedUrl",
        )
    }

    @Test
    fun `getUpload returns UploadDto on 200`() = runTest {
        val api = api { _ ->
            respond(UploadDtoFixtures.UPLOAD_DONE_JSON, HttpStatusCode.OK, jsonHeaders)
        }
        val result = api.getUpload("upload-2")
        assertTrue(result.isSuccess)
        assertEquals(UploadStatus.DONE, result.getOrThrow().status)
    }

    @Test
    fun `deleteUpload returns success on 204`() = runTest {
        val api = api { _ -> respond("", HttpStatusCode.NoContent) }
        assertTrue(api.deleteUpload("upload-1").isSuccess)
    }

    @Test
    fun `deleteUpload returns Conflict on 409 invalid-state-transition`() = runTest {
        val api = api { _ ->
            respond(MockResponses.PROBLEM_INVALID_STATE_TRANSITION, HttpStatusCode.Conflict, problemHeaders)
        }
        val result = api.deleteUpload("upload-done")
        assertIs<NetworkError.Conflict>(result.exceptionOrNull())
    }

    @Test
    fun `uploadPhoto sends multipart POST and returns 202 UploadDto`() = runTest {
        var capturedMethod: HttpMethod? = null
        val api = api { request ->
            capturedMethod = request.method
            respond(UploadDtoFixtures.UPLOAD_PROCESSING_JSON, HttpStatusCode.Accepted, jsonHeaders)
        }
        val result = api.uploadPhoto(
            bytes = byteArrayOf(0x01, 0x02, 0x03),
            fileName = "IMG_001.jpg",
            mimeType = "image/jpeg",
        )
        assertTrue(result.isSuccess)
        assertEquals(HttpMethod.Post, capturedMethod)
        assertEquals("upload-1", result.getOrThrow().id)
    }

    @Test
    fun `uploadPhoto returns NotFound on 404`() = runTest {
        val api = api { _ ->
            respond(MockResponses.PROBLEM_NOT_FOUND, HttpStatusCode.NotFound, problemHeaders)
        }
        val result = api.uploadPhoto(byteArrayOf(), "test.jpg", "image/jpeg")
        assertIs<NetworkError.NotFound>(result.exceptionOrNull())
    }
}