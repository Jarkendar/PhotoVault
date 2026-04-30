package dev.jarkendar.photovault.core.network.fixtures

import dev.jarkendar.photovault.core.network.dto.upload.UploadDto
import dev.jarkendar.photovault.core.network.dto.upload.UploadStatus
import kotlinx.datetime.Instant

object UploadDtoFixtures {

    val UPLOAD_PROCESSING = UploadDto(
        id = "upload-1",
        fileName = "IMG_001.jpg",
        sizeBytes = 4_195_000L,
        uploadedBytes = 4_195_000L,
        status = UploadStatus.PROCESSING,
        progress = 1.0,
        photoId = null,
        error = null,
        createdAt = Instant.parse("2026-04-18T17:24:30Z"),
    )

    val UPLOAD_PROCESSING_JSON = """
        {
          "id": "upload-1",
          "fileName": "IMG_001.jpg",
          "sizeBytes": 4195000,
          "uploadedBytes": 4195000,
          "status": "processing",
          "progress": 1.0,
          "photoId": null,
          "error": null,
          "createdAt": "2026-04-18T17:24:30Z"
        }
    """.trimIndent()

    val UPLOAD_DONE = UPLOAD_PROCESSING.copy(
        id = "upload-2",
        status = UploadStatus.DONE,
        photoId = "photo-abc123",
    )

    val UPLOAD_DONE_JSON = """
        {
          "id": "upload-2",
          "fileName": "IMG_001.jpg",
          "sizeBytes": 4195000,
          "uploadedBytes": 4195000,
          "status": "done",
          "progress": 1.0,
          "photoId": "photo-abc123",
          "createdAt": "2026-04-18T17:24:30Z"
        }
    """.trimIndent()

    val UPLOAD_FAILED = UPLOAD_PROCESSING.copy(
        id = "upload-3",
        status = UploadStatus.FAILED,
        error = "Decoder error",
    )

    val UPLOAD_FAILED_JSON = """
        {
          "id": "upload-3",
          "fileName": "IMG_001.jpg",
          "sizeBytes": 4195000,
          "uploadedBytes": 4195000,
          "status": "failed",
          "progress": 1.0,
          "error": "Decoder error",
          "createdAt": "2026-04-18T17:24:30Z"
        }
    """.trimIndent()
}