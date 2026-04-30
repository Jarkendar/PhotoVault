package dev.jarkendar.photovault.core.data.fixtures

import dev.jarkendar.photovault.core.database.entity.CategoryEntity
import dev.jarkendar.photovault.core.database.entity.LabelEntity
import dev.jarkendar.photovault.core.database.entity.PhotoEntity
import dev.jarkendar.photovault.core.database.entity.TagEntity
import dev.jarkendar.photovault.core.database.relation.PhotoWithRelations
import dev.jarkendar.photovault.core.domain.id.CategoryId
import dev.jarkendar.photovault.core.domain.id.LabelId
import dev.jarkendar.photovault.core.domain.id.PhotoId
import dev.jarkendar.photovault.core.domain.id.TagId
import dev.jarkendar.photovault.core.domain.model.Category
import dev.jarkendar.photovault.core.domain.model.GeoLocation
import dev.jarkendar.photovault.core.domain.model.Label
import dev.jarkendar.photovault.core.domain.model.Photo
import dev.jarkendar.photovault.core.domain.model.Tag
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

object PhotoTestFixtures {

    val CAPTURED_AT: Instant = Instant.parse("2026-04-18T17:24:00Z")
    val UPLOADED_AT: Instant = Instant.parse("2026-04-18T17:24:30Z")

    val TAG_DTO = TagDto(id = "tag-001", name = "#morze", photoCount = 48)
    val CATEGORY_DTO = CategoryDto(id = "cat-001", name = "Natura", colorHex = "#FF8B45", photoCount = 48)
    val LABEL_DTO = LabelDto(id = "label-orange", name = LabelName.ORANGE, colorHex = "#FF8B45", photoCount = 23)
    val USER_REF_DTO = UserRefDto(id = "user-jarek", displayName = "Jarek")
    val LOCATION_DTO = LocationDto(latitude = 54.4641, longitude = 18.5734, placeName = "Sopot, PL")

    val PHOTO_DTO = PhotoDto(
        id = "photo-abc123",
        name = "zachod_morze.jpg",
        sizeBytes = 4_195_000L,
        mimeType = "image/jpeg",
        width = 4032,
        height = 3024,
        capturedAt = CAPTURED_AT,
        uploadedAt = UPLOADED_AT,
        camera = "Pixel 8 Pro",
        location = LOCATION_DTO,
        uploadedBy = USER_REF_DTO,
        tags = listOf(TAG_DTO),
        categories = listOf(CATEGORY_DTO),
        labels = listOf(LABEL_DTO),
        isFavorite = true,
        processingStatus = ProcessingStatus.READY,
        thumbnailUrl = "/v1/photos/photo-abc123/thumbnail",
        mediumUrl = "/v1/photos/photo-abc123/medium",
        originalUrl = "/v1/photos/photo-abc123/original",
    )

    val PHOTO_DTO_NO_OPTIONALS = PHOTO_DTO.copy(
        id = "photo-min",
        capturedAt = null,
        camera = null,
        location = null,
        tags = emptyList(),
        categories = emptyList(),
        labels = emptyList(),
        isFavorite = false,
        processingStatus = ProcessingStatus.PROCESSING,
    )

    val PHOTO_PAGE_DTO = PhotoPageDto(
        items = listOf(PHOTO_DTO, PHOTO_DTO_NO_OPTIONALS),
        nextCursor = null,
        hasMore = false,
    )

    val TAG_DOMAIN = Tag(id = TagId("tag-001"), name = "#morze")
    val CATEGORY_DOMAIN = Category(id = CategoryId("cat-001"), name = "Natura", colorHex = "#FF8B45")
    val LABEL_DOMAIN = Label(id = LabelId("label-orange"), name = "orange", colorHex = "#FF8B45")
    val LOCATION_DOMAIN = GeoLocation(latitude = 54.4641, longitude = 18.5734, placeName = "Sopot, PL")

    val PHOTO_DOMAIN = Photo(
        id = PhotoId("photo-abc123"),
        name = "zachod_morze.jpg",
        sizeBytes = 4_195_000L,
        mimeType = "image/jpeg",
        width = 4032,
        height = 3024,
        capturedAt = CAPTURED_AT,
        uploadedAt = UPLOADED_AT,
        camera = "Pixel 8 Pro",
        location = LOCATION_DOMAIN,
        tags = listOf(TAG_DOMAIN),
        categories = listOf(CATEGORY_DOMAIN),
        labels = listOf(LABEL_DOMAIN),
        isFavorite = true,
    )

    val TAG_ENTITY = TagEntity(id = "tag-001", name = "#morze")
    val CATEGORY_ENTITY = CategoryEntity(id = "cat-001", name = "Natura", colorHex = "#FF8B45")
    val LABEL_ENTITY = LabelEntity(id = "label-orange", name = "orange", colorHex = "#FF8B45")

    val PHOTO_ENTITY = PhotoEntity(
        id = "photo-abc123",
        name = "zachod_morze.jpg",
        sizeBytes = 4_195_000L,
        mimeType = "image/jpeg",
        width = 4032,
        height = 3024,
        capturedAt = CAPTURED_AT,
        uploadedAt = UPLOADED_AT,
        camera = "Pixel 8 Pro",
        latitude = 54.4641,
        longitude = 18.5734,
        placeName = "Sopot, PL",
        isFavorite = true,
    )

    val PHOTO_ENTITY_NO_OPTIONALS = PhotoEntity(
        id = "photo-min",
        name = "scan.jpg",
        sizeBytes = 100_000L,
        mimeType = "image/jpeg",
        width = 800,
        height = 600,
        capturedAt = null,
        uploadedAt = UPLOADED_AT,
        camera = null,
        latitude = null,
        longitude = null,
        placeName = null,
        isFavorite = false,
    )

    val PHOTO_WITH_RELATIONS = PhotoWithRelations(
        photo = PHOTO_ENTITY,
        tags = listOf(TAG_ENTITY),
        categories = listOf(CATEGORY_ENTITY),
        labels = listOf(LABEL_ENTITY),
    )

    val PHOTO_WITH_RELATIONS_NO_OPTIONALS = PhotoWithRelations(
        photo = PHOTO_ENTITY_NO_OPTIONALS,
        tags = emptyList(),
        categories = emptyList(),
        labels = emptyList(),
    )
}