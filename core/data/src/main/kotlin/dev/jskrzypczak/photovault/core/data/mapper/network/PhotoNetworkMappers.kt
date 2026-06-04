package dev.jskrzypczak.photovault.core.data.mapper.network

import dev.jskrzypczak.photovault.core.domain.id.PhotoId
import dev.jskrzypczak.photovault.core.domain.model.GeoLocation
import dev.jskrzypczak.photovault.core.domain.model.Photo
import dev.jskrzypczak.photovault.core.domain.model.ProcessingStatus
import kotlinx.collections.immutable.toImmutableList
import dev.jskrzypczak.photovault.core.network.dto.photo.LocationDto
import dev.jskrzypczak.photovault.core.network.dto.photo.PhotoDto
import dev.jskrzypczak.photovault.core.network.dto.photo.PhotoPageDto
import dev.jskrzypczak.photovault.core.network.dto.photo.ProcessingStatus as NetworkProcessingStatus

internal fun PhotoDto.toDomain(): Photo = Photo(
    id = PhotoId(id),
    name = name,
    sizeBytes = sizeBytes,
    mimeType = mimeType,
    width = width,
    height = height,
    capturedAt = capturedAt,
    uploadedAt = uploadedAt,
    camera = camera,
    location = location?.toDomain(),
    tags = tags.map { it.toDomain() }.toImmutableList(),
    categories = categories.map { it.toDomain() }.toImmutableList(),
    labels = labels.map { it.toDomain() }.toImmutableList(),
    isFavorite = isFavorite,
    processingStatus = processingStatus.toDomain(),
    thumbnailUrl = thumbnailUrl,
    mediumUrl = mediumUrl,
    originalUrl = originalUrl,
)

internal fun LocationDto.toDomain(): GeoLocation = GeoLocation(
    latitude = latitude,
    longitude = longitude,
    placeName = placeName,
)

internal fun PhotoPageDto.toDomain(): List<Photo> = items.map { it.toDomain() }

internal fun NetworkProcessingStatus.toDomain(): ProcessingStatus = when (this) {
    NetworkProcessingStatus.PROCESSING -> ProcessingStatus.PROCESSING
    NetworkProcessingStatus.PENDING_CATEGORIZATION -> ProcessingStatus.PENDING_CATEGORIZATION
    NetworkProcessingStatus.READY -> ProcessingStatus.READY
}