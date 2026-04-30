package dev.jarkendar.photovault.core.network.fixtures

import dev.jarkendar.photovault.core.network.dto.category.CategoryDto
import dev.jarkendar.photovault.core.network.dto.label.LabelDto
import dev.jarkendar.photovault.core.network.dto.label.LabelName
import dev.jarkendar.photovault.core.network.dto.photo.LocationDto
import dev.jarkendar.photovault.core.network.dto.photo.PhotoDto
import dev.jarkendar.photovault.core.network.dto.photo.PhotoPageDto
import dev.jarkendar.photovault.core.network.dto.photo.ProcessingStatus
import dev.jarkendar.photovault.core.network.dto.tag.TagDto
import dev.jarkendar.photovault.core.network.dto.user.UserRefDto
import kotlinx.datetime.Instant

object PhotoDtoFixtures {

    val SAMPLE_USER_REF = UserRefDto(id = "user-jarek", displayName = "Jarek")

    val SAMPLE_TAG = TagDto(id = "tag-001", name = "#morze", photoCount = 48)
    val SAMPLE_CATEGORY = CategoryDto(id = "cat-001", name = "Natura", colorHex = "#FF8B45", photoCount = 48)
    val SAMPLE_LABEL = LabelDto(id = "label-orange", name = LabelName.ORANGE, colorHex = "#FF8B45", photoCount = 23)

    val SAMPLE_LOCATION = LocationDto(latitude = 54.4641, longitude = 18.5734, placeName = "Sopot, PL")

    val SAMPLE_PHOTO_DTO = PhotoDto(
        id = "photo-abc123",
        name = "zachod_morze.jpg",
        sizeBytes = 4_195_000L,
        mimeType = "image/jpeg",
        width = 4032,
        height = 3024,
        capturedAt = Instant.parse("2026-04-18T17:24:00Z"),
        uploadedAt = Instant.parse("2026-04-18T17:24:30Z"),
        camera = "Pixel 8 Pro",
        location = SAMPLE_LOCATION,
        uploadedBy = SAMPLE_USER_REF,
        tags = listOf(SAMPLE_TAG),
        categories = listOf(SAMPLE_CATEGORY),
        labels = listOf(SAMPLE_LABEL),
        isFavorite = true,
        processingStatus = ProcessingStatus.READY,
        thumbnailUrl = "/v1/photos/photo-abc123/thumbnail",
        mediumUrl = "/v1/photos/photo-abc123/medium",
        originalUrl = "/v1/photos/photo-abc123/original",
    )

    val SAMPLE_PHOTO_JSON = """
        {
          "id": "photo-abc123",
          "name": "zachod_morze.jpg",
          "sizeBytes": 4195000,
          "mimeType": "image/jpeg",
          "width": 4032,
          "height": 3024,
          "capturedAt": "2026-04-18T17:24:00Z",
          "uploadedAt": "2026-04-18T17:24:30Z",
          "camera": "Pixel 8 Pro",
          "location": {
            "latitude": 54.4641,
            "longitude": 18.5734,
            "placeName": "Sopot, PL"
          },
          "uploadedBy": { "id": "user-jarek", "displayName": "Jarek" },
          "tags": [{ "id": "tag-001", "name": "#morze", "photoCount": 48 }],
          "categories": [{ "id": "cat-001", "name": "Natura", "colorHex": "#FF8B45", "photoCount": 48 }],
          "labels": [{ "id": "label-orange", "name": "orange", "colorHex": "#FF8B45", "photoCount": 23 }],
          "isFavorite": true,
          "processingStatus": "ready",
          "thumbnailUrl": "/v1/photos/photo-abc123/thumbnail",
          "mediumUrl": "/v1/photos/photo-abc123/medium",
          "originalUrl": "/v1/photos/photo-abc123/original"
        }
    """.trimIndent()

    val SAMPLE_PHOTO_NO_OPTIONALS_DTO = PhotoDto(
        id = "photo-def456",
        name = "scan.jpg",
        sizeBytes = 100_000L,
        mimeType = "image/jpeg",
        width = 800,
        height = 600,
        capturedAt = null,
        uploadedAt = Instant.parse("2026-01-01T00:00:00Z"),
        camera = null,
        location = null,
        uploadedBy = SAMPLE_USER_REF,
        tags = emptyList(),
        categories = emptyList(),
        labels = emptyList(),
        isFavorite = false,
        processingStatus = ProcessingStatus.PROCESSING,
        thumbnailUrl = "/v1/photos/photo-def456/thumbnail",
        mediumUrl = "/v1/photos/photo-def456/medium",
        originalUrl = "/v1/photos/photo-def456/original",
    )

    val SAMPLE_PHOTO_NO_OPTIONALS_JSON = """
        {
          "id": "photo-def456",
          "name": "scan.jpg",
          "sizeBytes": 100000,
          "mimeType": "image/jpeg",
          "width": 800,
          "height": 600,
          "capturedAt": null,
          "uploadedAt": "2026-01-01T00:00:00Z",
          "camera": null,
          "location": null,
          "uploadedBy": { "id": "user-jarek", "displayName": "Jarek" },
          "tags": [],
          "categories": [],
          "labels": [],
          "isFavorite": false,
          "processingStatus": "processing",
          "thumbnailUrl": "/v1/photos/photo-def456/thumbnail",
          "mediumUrl": "/v1/photos/photo-def456/medium",
          "originalUrl": "/v1/photos/photo-def456/original"
        }
    """.trimIndent()

    val SAMPLE_PHOTO_PAGE_DTO = PhotoPageDto(
        items = listOf(SAMPLE_PHOTO_DTO),
        nextCursor = "cursor-abc",
        hasMore = true,
    )

    val SAMPLE_PHOTO_PAGE_JSON = """
        {
          "items": [$SAMPLE_PHOTO_JSON],
          "nextCursor": "cursor-abc",
          "hasMore": true
        }
    """.trimIndent()

    val EMPTY_PHOTO_PAGE_JSON = """
        {
          "items": [],
          "nextCursor": null,
          "hasMore": false
        }
    """.trimIndent()
}
