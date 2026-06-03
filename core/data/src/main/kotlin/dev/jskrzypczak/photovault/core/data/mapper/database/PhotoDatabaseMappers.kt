package dev.jskrzypczak.photovault.core.data.mapper.database

import dev.jskrzypczak.photovault.core.database.entity.PhotoEntity
import dev.jskrzypczak.photovault.core.database.relation.PhotoWithRelations
import dev.jskrzypczak.photovault.core.domain.id.PhotoId
import dev.jskrzypczak.photovault.core.domain.model.GeoLocation
import dev.jskrzypczak.photovault.core.domain.model.Photo
import kotlinx.collections.immutable.toImmutableList

internal fun PhotoWithRelations.toDomain(): Photo = Photo(
    id = PhotoId(photo.id),
    name = photo.name,
    sizeBytes = photo.sizeBytes,
    mimeType = photo.mimeType,
    width = photo.width,
    height = photo.height,
    capturedAt = photo.capturedAt,
    uploadedAt = photo.uploadedAt,
    camera = photo.camera,
    location = photo.toGeoLocation(),
    tags = tags.map { it.toDomain() }.toImmutableList(),
    categories = categories.map { it.toDomain() }.toImmutableList(),
    labels = labels.map { it.toDomain() }.toImmutableList(),
    isFavorite = photo.isFavorite,
    thumbnailUrl = photo.thumbnailUrl,
    mediumUrl = photo.mediumUrl,
    originalUrl = photo.originalUrl,
)

internal fun Photo.toEntity(): PhotoEntity = PhotoEntity(
    id = id.value,
    name = name,
    sizeBytes = sizeBytes,
    mimeType = mimeType,
    width = width,
    height = height,
    capturedAt = capturedAt,
    uploadedAt = uploadedAt,
    camera = camera,
    latitude = location?.latitude,
    longitude = location?.longitude,
    placeName = location?.placeName,
    isFavorite = isFavorite,
    thumbnailUrl = thumbnailUrl,
    mediumUrl = mediumUrl,
    originalUrl = originalUrl,
)

private fun PhotoEntity.toGeoLocation(): GeoLocation? {
    val lat = latitude ?: return null
    val lng = longitude ?: return null
    return GeoLocation(latitude = lat, longitude = lng, placeName = placeName)
}