package dev.jskrzypczak.photovault.core.data.mapper.network

import dev.jskrzypczak.photovault.core.domain.model.UploadJobStatus
import dev.jskrzypczak.photovault.core.network.dto.upload.UploadDto
import dev.jskrzypczak.photovault.core.network.dto.upload.UploadStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Instant

private val CREATED_AT = Instant.parse("2026-04-18T17:24:30Z")

private val UPLOAD_PROCESSING = UploadDto(
    id = "upload-1",
    fileName = "IMG_001.jpg",
    sizeBytes = 4_195_000L,
    uploadedBytes = 4_195_000L,
    status = UploadStatus.PROCESSING,
    progress = 0.65,
    photoId = null,
    error = null,
    createdAt = CREATED_AT,
)

private val UPLOAD_DONE = UPLOAD_PROCESSING.copy(
    id = "upload-2",
    status = UploadStatus.DONE,
    progress = 1.0,
    photoId = "photo-abc123",
)

private val UPLOAD_FAILED = UPLOAD_PROCESSING.copy(
    id = "upload-3",
    status = UploadStatus.FAILED,
    error = "Decoder error",
)

class UploadNetworkMapperTest {

    @Test
    fun `toDomain maps all scalar fields correctly`() {
        val domain = UPLOAD_DONE.toDomain()
        assertEquals("upload-2", domain.id)
        assertEquals("IMG_001.jpg", domain.fileName)
        assertEquals(4_195_000L, domain.sizeBytes)
        assertEquals(4_195_000L, domain.uploadedBytes)
        assertEquals(1.0, domain.progress)
        assertEquals("photo-abc123", domain.photoId)
        assertEquals(UploadJobStatus.DONE, domain.status)
    }

    @Test
    fun `toDomain maps null photoId`() {
        assertNull(UPLOAD_PROCESSING.toDomain().photoId)
    }

    @Test
    fun `toDomain maps error field`() {
        val domain = UPLOAD_FAILED.toDomain()
        assertEquals("Decoder error", domain.error)
        assertEquals(UploadJobStatus.FAILED, domain.status)
    }

    @Test
    fun `UploadStatus toDomain maps all values`() {
        assertEquals(UploadJobStatus.CREATED, UploadStatus.CREATED.toDomain())
        assertEquals(UploadJobStatus.UPLOADING, UploadStatus.UPLOADING.toDomain())
        assertEquals(UploadJobStatus.PROCESSING, UploadStatus.PROCESSING.toDomain())
        assertEquals(UploadJobStatus.DONE, UploadStatus.DONE.toDomain())
        assertEquals(UploadJobStatus.FAILED, UploadStatus.FAILED.toDomain())
        assertEquals(UploadJobStatus.CANCELLED, UploadStatus.CANCELLED.toDomain())
    }

    @Test
    fun `UploadJobStatus toDto maps all values`() {
        assertEquals(UploadStatus.CREATED, UploadJobStatus.CREATED.toDto())
        assertEquals(UploadStatus.UPLOADING, UploadJobStatus.UPLOADING.toDto())
        assertEquals(UploadStatus.PROCESSING, UploadJobStatus.PROCESSING.toDto())
        assertEquals(UploadStatus.DONE, UploadJobStatus.DONE.toDto())
        assertEquals(UploadStatus.FAILED, UploadJobStatus.FAILED.toDto())
        assertEquals(UploadStatus.CANCELLED, UploadJobStatus.CANCELLED.toDto())
    }

    @Test
    fun `toDomain and toDto are inverse for all statuses`() {
        UploadStatus.entries.forEach { dto ->
            assertEquals(dto, dto.toDomain().toDto(), "round-trip failed for $dto")
        }
    }
}
