package dev.jskrzypczak.photovault.core.data.repository

import dev.jskrzypczak.photovault.core.data.fakes.FakeUploadsApi
import dev.jskrzypczak.photovault.core.domain.model.UploadJobStatus
import dev.jskrzypczak.photovault.core.network.dto.upload.UploadDto
import dev.jskrzypczak.photovault.core.network.dto.upload.UploadListDto
import dev.jskrzypczak.photovault.core.network.dto.upload.UploadStatus
import dev.jskrzypczak.photovault.core.network.error.NetworkError
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Instant

private val BASE_DTO = UploadDto(
    id = "upload-1",
    fileName = "IMG_001.jpg",
    sizeBytes = 4_195_000L,
    uploadedBytes = 4_195_000L,
    status = UploadStatus.PROCESSING,
    progress = 0.65,
    createdAt = Instant.parse("2026-04-18T17:24:30Z"),
)

class UploadRepositoryImplTest {

    private fun repo(api: FakeUploadsApi = FakeUploadsApi()) =
        UploadRepositoryImpl(api) to api

    @Test
    fun `uploadPhoto delegates to api and maps result`() = runTest {
        val (repo, api) = repo()
        api.nextUploadResponse = Result.success(BASE_DTO)

        val result = repo.uploadPhoto(byteArrayOf(1, 2, 3), "IMG_001.jpg", "image/jpeg")

        assertTrue(result.isSuccess)
        assertEquals("upload-1", result.getOrThrow().id)
        assertEquals(UploadJobStatus.PROCESSING, result.getOrThrow().status)
        assertEquals(1, api.uploadCalls.size)
        assertEquals("IMG_001.jpg", api.uploadCalls[0].fileName)
        assertEquals(3, api.uploadCalls[0].byteCount)
    }

    @Test
    fun `uploadPhoto propagates network error`() = runTest {
        val (repo, api) = repo()
        api.nextUploadResponse = Result.failure(
            NetworkError.ServerError(500, "about:blank", null),
        )

        val result = repo.uploadPhoto(byteArrayOf(), "test.jpg", "image/jpeg")

        assertIs<NetworkError.ServerError>(result.exceptionOrNull())
    }

    @Test
    fun `getUploadStatus delegates to api with correct id`() = runTest {
        val (repo, api) = repo()
        api.nextGetResponse = Result.success(BASE_DTO.copy(status = UploadStatus.DONE, photoId = "photo-xyz"))

        val result = repo.getUploadStatus("upload-1")

        assertTrue(result.isSuccess)
        assertEquals(UploadJobStatus.DONE, result.getOrThrow().status)
        assertEquals("photo-xyz", result.getOrThrow().photoId)
        assertEquals(listOf("upload-1"), api.getCalls)
    }

    @Test
    fun `cancelUpload delegates to api`() = runTest {
        val (repo, api) = repo()

        val result = repo.cancelUpload("upload-1")

        assertTrue(result.isSuccess)
        assertEquals(listOf("upload-1"), api.deleteCalls)
    }

    @Test
    fun `cancelUpload propagates error`() = runTest {
        val (repo, api) = repo()
        api.nextDeleteResponse = Result.failure(
            NetworkError.Conflict("invalid-state-transition", "Cannot cancel done upload"),
        )

        val result = repo.cancelUpload("upload-done")

        assertIs<NetworkError.Conflict>(result.exceptionOrNull())
    }

    @Test
    fun `listUploads returns all items mapped`() = runTest {
        val (repo, api) = repo()
        api.nextListResponse = Result.success(
            UploadListDto(listOf(BASE_DTO, BASE_DTO.copy(id = "upload-2"))),
        )

        val result = repo.listUploads()

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrThrow().size)
    }

    @Test
    fun `listUploads with status filter passes mapped statuses to api`() = runTest {
        val api = FakeUploadsApi()
        val repo = UploadRepositoryImpl(api)
        api.nextListResponse = Result.success(UploadListDto(emptyList()))

        repo.listUploads(statuses = listOf(UploadJobStatus.DONE, UploadJobStatus.FAILED))

        assertEquals(listOf(UploadStatus.DONE, UploadStatus.FAILED), api.capturedListStatuses)
    }
}
